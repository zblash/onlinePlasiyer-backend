package com.marketing.web.utils.facade.impl;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.dtos.cart.WritableCheckout;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableConfirmOrder;
import com.marketing.web.dtos.order.WritableConfirmOrderItem;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.enums.*;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.services.credit.ActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.invoice.ObligationActivityService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.order.OrderItemService;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "orderFacade")
public class OrderFacadeImpl implements OrderFacade {

    private final OrderService orderService;

    private final MerchantService merchantService;

    private final OrderItemService orderItemService;

    private final ObligationService obligationService;

    private final ObligationActivityService obligationActivityService;

    private final CreditService creditService;

    private final ActivityService activityService;

    private final ProductSpecifyService productSpecifyService;

    private Logger logger = LoggerFactory.getLogger(OrderFacadeImpl.class);

    public OrderFacadeImpl(OrderService orderService, MerchantService merchantService, OrderItemService orderItemService, ObligationService obligationService, ObligationActivityService obligationActivityService, CreditService creditService, ActivityService activityService, ProductSpecifyService productSpecifyService) {
        this.orderService = orderService;
        this.merchantService = merchantService;
        this.orderItemService = orderItemService;
        this.obligationService = obligationService;
        this.obligationActivityService = obligationActivityService;
        this.creditService = creditService;
        this.activityService = activityService;
        this.productSpecifyService = productSpecifyService;
    }

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Order order) {
        if ((OrderStatus.CONFIRMED.equals(order.getStatus()) || OrderStatus.PREPARED.equals(order.getStatus()))
                && OrderStatus.FINISHED.equals(writableOrder.getStatus())
                && writableOrder.getWaybillDate() != null) {
            Credit credit;
            Optional<Credit> optionalMerchantCredit = creditService.findByCustomerAndMerchant(order.getCustomer(), order.getMerchant());
            order.setCommentable(true);
            if (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) && writableOrder.getPaidPrice().compareTo(BigDecimal.ZERO) > 0) {
                Credit systemCredit = creditService.findSystemCreditByCustomer(order.getCustomer());
                systemCredit.setTotalDebt(systemCredit.getTotalDebt().subtract(writableOrder.getPaidPrice()));
                Obligation obligation = obligationService.findByMerchant(order.getMerchant());
                obligation.setReceivable(obligation.getReceivable().subtract(writableOrder.getPaidPrice()));
                obligationService.update(obligation.getId().toString(), obligation);
                creditService.update(systemCredit.getId().toString(), systemCredit);

                activityService.create(activityService.populator(order.getCustomer(), null, writableOrder.getPaidPrice(), order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add), systemCredit.getTotalDebt(), systemCredit.getCreditLimit().subtract(systemCredit.getTotalDebt()), systemCredit.getCreditLimit(), writableOrder.getPaymentType() != null ? writableOrder.getPaymentType() : PaymentType.RUNNING_ACCOUNT, ActivityType.SYSTEM_CREDIT));

                credit = optionalMerchantCredit.orElseGet(() -> {
                    Credit c = new Credit();
                    c.setCreditType(CreditType.MERCHANT_CREDIT);
                    return c;
                });

            } else if (PaymentOption.MERCHANT_CREDIT.equals(order.getPaymentType()) && writableOrder.getPaidPrice().compareTo(BigDecimal.ZERO) > 0) {
                credit = optionalMerchantCredit
                        .orElseThrow(() -> new BadRequestException("You have not credit from this merchant"));
                credit.setTotalDebt(credit.getTotalDebt().subtract(writableOrder.getPaidPrice()));
                creditService.update(credit.getId().toString(), credit);

            } else if (PaymentOption.COD.equals(order.getPaymentType()) && writableOrder.getPaidPrice() != order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)) {
                BigDecimal totalPrice = BigDecimal.valueOf(Math.abs(writableOrder.getPaidPrice().subtract(order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)).doubleValue()));
                boolean isDebt = writableOrder.getPaidPrice().compareTo(order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)) < 0;

                if (optionalMerchantCredit.isPresent()) {
                    credit = optionalMerchantCredit.get();
                    credit.setTotalDebt(isDebt ?
                            credit.getTotalDebt().add(totalPrice) :
                            credit.getTotalDebt().subtract(totalPrice));
                    creditService.update(credit.getId().toString(), credit);
                } else {
                    credit = creditService.create(Credit.builder()
                            .creditLimit(totalPrice)
                            .totalDebt(isDebt ?
                                    totalPrice : BigDecimal.ZERO)
                            .creditType(CreditType.MERCHANT_CREDIT)
                            .customer(order.getCustomer()).merchant(order.getMerchant()).build());
                }

            } else {
                credit = optionalMerchantCredit.orElseGet(() -> {
                    Credit c = new Credit();
                    c.setCreditType(CreditType.MERCHANT_CREDIT);
                    c.setTotalDebt(BigDecimal.ZERO);
                    c.setCreditLimit(BigDecimal.ZERO);
                    c.setCustomer(order.getCustomer());
                    c.setMerchant(order.getMerchant());
                    return creditService.create(c);
                });
            }

            activityService.create(activityService.populator(order.getCustomer(), order.getMerchant(), writableOrder.getPaidPrice(), order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add), credit.getTotalDebt(), credit.getCreditLimit().subtract(credit.getTotalDebt()), credit.getCreditLimit(), writableOrder.getPaymentType() != null ? writableOrder.getPaymentType() : PaymentType.RUNNING_ACCOUNT, ActivityType.ORDER));
            updateProductsStock(order.getOrderItems());
        } else if (OrderStatus.CANCELLED.equals(writableOrder.getStatus())) {
            Obligation obligation = obligationService.findByMerchant(order.getMerchant());
            if (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType())) {
                obligation.setReceivable(obligation.getReceivable().subtract(BigDecimal.valueOf(order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum())));
            } else {
                obligation.setDebt(obligation.getDebt().subtract(BigDecimal.valueOf(order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum())));
            }
            obligationService.update(obligation.getId().toString(), obligation);

            obligationActivityService.create(obligationActivityService.populator(obligation, order));
            Credit credit = PaymentOption.MERCHANT_CREDIT.equals(order.getPaymentType()) ? creditService.findByCustomerAndMerchant(order.getCustomer(), order.getMerchant())
                    .orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND + "credit.user", ""))
                    : (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) ? creditService.findSystemCreditByCustomer(order.getCustomer()) : null);
            if (credit != null) {
                credit.setTotalDebt(credit.getTotalDebt().subtract(order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)));

                activityService.create(activityService.populator(order.getCustomer(), order.getMerchant(), BigDecimal.ZERO, order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add), credit.getTotalDebt(), credit.getCreditLimit().subtract(credit.getTotalDebt()), credit.getCreditLimit(), null, ActivityType.ORDER_CANCEL));
                creditService.update(credit.getId().toString(), credit);
            }
        }

        order.setStatus(writableOrder.getStatus());

        if (writableOrder.getWaybillDate() != null) {
            order.setWaybillDate(writableOrder.getWaybillDate());
        }
        return OrderMapper.orderToReadableOrder(orderService.update(order.getId().toString(), order));
    }

    @Override
    public ReadableOrder confirmOrder(WritableConfirmOrder writableConfirmOrder, Order order) {
        if (writableConfirmOrder.getItems().stream().allMatch(WritableConfirmOrderItem::isRemoved)) {
            throw new BadRequestException("You can not remove all orders");
        }
        if (!order.getStatus().equals(OrderStatus.NEW) && !order.getStatus().equals(OrderStatus.CANCEL_REQUEST)) {
            throw new BadRequestException("You can not confirm this order");
        }
        List<OrderItem> removedItems = new ArrayList<>();
        List<OrderItem> updatedItems = new ArrayList<>();
        for (WritableConfirmOrderItem writableConfirmOrderItem : writableConfirmOrder.getItems()) {
            OrderItem orderItem = orderItemService.findById(writableConfirmOrderItem.getId());
            if (writableConfirmOrderItem.isRemoved()) {
                removedItems.add(orderItem);
            } else {
                OrderItem updatedOrderItem = calculateOrderItem(orderItem, writableConfirmOrderItem);
                updatedItems.add(updatedOrderItem);
            }
        }
        orderItemService.saveAll(updatedItems);
        orderItemService.deleteAllByUuid(removedItems);
        order.setStatus(OrderStatus.CONFIRMED);

        if (writableConfirmOrder.getWaybillDate() != null) {
            order.setWaybillDate(writableConfirmOrder.getWaybillDate());
        }


        if (order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(updatedItems.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)) != 0) {
            double oldCommission = order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum();
            Obligation obligation = obligationService.findByMerchant(order.getMerchant());
            obligationService.update(obligation.getId().toString(), calculateObligation(order, obligation, oldCommission));
            obligationActivityService.update(obligationActivityService.findByOrder(order).getId().toString(), obligationActivityService.populator(obligation, order));
        }
        Credit credit = order.getPaymentType().equals(PaymentOption.MERCHANT_CREDIT)
                ? creditService.findByCustomerAndMerchant(order.getCustomer(), order.getMerchant())
                .orElseThrow(() -> new BadRequestException("You have not credit from this merchant"))
                : creditService.findSystemCreditByCustomer(order.getCustomer());
        if (!PaymentOption.COD.equals(order.getPaymentType())) {
            credit.setTotalDebt(credit.getTotalDebt().add(order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
            creditService.update(credit.getId().toString(), credit);
        }

        return OrderMapper.orderToReadableOrder(orderService.update(order.getId().toString(), order));
    }

    @Override
    public List<ReadableOrder> checkoutCart(Customer customer, Set<CartItemHolder> cartItemHolderList, WritableCheckout writableCheckout) {
        {
            List<Order> orders = new ArrayList<>();
            List<OrderItem> orderItems = new ArrayList<>();
            List<Obligation> obligations = new ArrayList<>();
            List<ObligationActivity> obligationActivities = new ArrayList<>();
            for (CartItemHolder cartItemHolder : cartItemHolderList) {
                Order order = ordersPopulator(cartItemHolder, customer);
                order.setOrderItems(orderItemsPopulator(new HashSet<>(cartItemHolder.getCartItems()), order));
                orders.add(order);
                Obligation obligation = calculateObligation(order, obligationService.findByMerchant(order.getMerchant()), 0);
                obligations.add(obligation);

                obligationActivities.add(obligationActivityService.populator(obligation, order));
                orderItems.addAll(order.getOrderItems());

            }
            orderService.createAll(orders);
            orderItemService.saveAll(orderItems);
            obligationService.createAll(obligations);
            obligationActivityService.saveAll(obligationActivities);

            return orders.stream().map(OrderMapper::orderToReadableOrder).collect(Collectors.toList());
        }
    }

    private Order ordersPopulator(CartItemHolder cartItemHolder, Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setMerchant(merchantService.findById(cartItemHolder.getMerchantId()));
        order.setOrderDate(LocalDate.now());
        order.setPaymentType(cartItemHolder.getPaymentOption());
        order.setStatus(OrderStatus.NEW);
        return order;
    }

    private Obligation calculateObligation(Order order, Obligation obligation, double oldCommission) {
        double commission = Math.abs(order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum() - oldCommission);
        boolean paymentType = order.getPaymentType().equals(PaymentOption.COD) || order.getPaymentType().equals(PaymentOption.MERCHANT_CREDIT);
        obligation.setDebt(paymentType ? obligation.getDebt().add(BigDecimal.valueOf(commission)) : obligation.getDebt());
        obligation.setReceivable(paymentType ? obligation.getReceivable() : obligation.getReceivable().subtract(order.getOrderItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(BigDecimal.valueOf(commission))));
        return obligation;
    }

    private List<OrderItem> orderItemsPopulator(Set<CartItem> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderMapper.cartItemToOrderItem(cartItem);
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private OrderItem calculateOrderItem(OrderItem orderItem, WritableConfirmOrderItem writableConfirmOrderItem) {
        orderItem.setQuantity(writableConfirmOrderItem.getQuantity());
        orderItem.setTotalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(writableConfirmOrderItem.getQuantity())));
        orderItem.setCommission((orderItem.getTotalPrice().doubleValue() * orderItem.getProductSpecify().getCommission()) / 100);
        return orderItem;
    }

    private void updateProductsStock(List<OrderItem> orderItems) {
        List<ProductSpecify> productSpecifies = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            ProductSpecify productSpecify = orderItem.getProductSpecify();
            productSpecify.setQuantity(productSpecify.getQuantity() - orderItem.getQuantity());
            productSpecifies.add(productSpecify);
        }
        productSpecifyService.updateAll(productSpecifies);
    }

}
