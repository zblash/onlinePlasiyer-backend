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
import com.marketing.web.services.credit.CreditActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.invoice.ObligationActivityService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.order.OrderItemService;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service(value = "orderFacade")
public class OrderFacadeImpl implements OrderFacade {

    private final OrderService orderService;

    private final UserService userService;

    private final OrderItemService orderItemService;

    private final ObligationService obligationService;

    private final ObligationActivityService obligationActivityService;

    private final CreditService creditService;

    private final ActivityService activityService;

    private Logger logger = LoggerFactory.getLogger(OrderFacadeImpl.class);

    public OrderFacadeImpl(OrderService orderService, UserService userService, OrderItemService orderItemService, ObligationService obligationService, ObligationActivityService obligationActivityService, CreditService creditService, ActivityService activityService) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderItemService = orderItemService;
        this.obligationService = obligationService;
        this.obligationActivityService = obligationActivityService;
        this.creditService = creditService;
        this.activityService = activityService;
    }

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Order order) {
        if (OrderStatus.CONFIRMED.equals(order.getStatus())
                && OrderStatus.FINISHED.equals(writableOrder.getStatus())
                && writableOrder.getWaybillDate() != null) {

            order.setWaybillDate(writableOrder.getWaybillDate());
            if (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) && writableOrder.getPaidPrice() > 0) {
                Credit credit = creditService.findSystemCreditByUser(order.getBuyer());
                credit.setTotalDebt(credit.getTotalDebt() - writableOrder.getPaidPrice());
                Obligation obligation = obligationService.findByUser(order.getSeller());
                obligation.setReceivable(obligation.getReceivable() - writableOrder.getPaidPrice());
                obligationService.update(obligation.getUuid().toString(), obligation);
                creditService.update(credit.getUuid().toString(), credit);
                activityService.create(activityPopulator(order.getBuyer(), order.getSeller(), writableOrder.getPaidPrice(), credit.getTotalDebt(), credit.getCreditLimit() - credit.getTotalDebt(), credit.getCreditLimit(), writableOrder.getPaymentType(), ActivityType.SYSTEM_CREDIT));
            } else if (PaymentOption.MERCHANT_CREDIT.equals(order.getPaymentType()) && writableOrder.getPaidPrice() > 0) {
                Credit credit = creditService.findByCustomerAndMerchant(order.getBuyer(), order.getSeller())
                        .orElseThrow(() -> new BadRequestException("You have not credit from this merchant"));
                credit.setTotalDebt(credit.getTotalDebt() - writableOrder.getPaidPrice());
                creditService.update(credit.getUuid().toString(), credit);
                activityService.create(activityPopulator(order.getBuyer(), order.getSeller(), writableOrder.getPaidPrice(), credit.getTotalDebt(), credit.getCreditLimit() - credit.getTotalDebt(), credit.getCreditLimit(), writableOrder.getPaymentType(), ActivityType.MERCHANT_CREDIT));
            } else if (PaymentOption.COD.equals(order.getPaymentType()) && writableOrder.getPaidPrice() != order.getTotalPrice()) {
                double totalPrice = Math.abs(writableOrder.getPaidPrice() - order.getTotalPrice());
                CreditActivityType creditActivityType = writableOrder.getPaidPrice() > order.getTotalPrice() ?
                        CreditActivityType.CREDIT : CreditActivityType.DEBT;

                Optional<Credit> creditOptional = creditService.findByCustomerAndMerchant(order.getBuyer(), order.getSeller());
                Credit credit;
                if (creditOptional.isPresent()) {
                    credit = creditOptional.get();
                    double debt = creditActivityType.equals(CreditActivityType.DEBT) ?
                            credit.getTotalDebt() + totalPrice :
                            credit.getTotalDebt() - totalPrice;
                    credit.setTotalDebt(debt);
                    creditService.update(credit.getUuid().toString(), credit);
                } else {
                    credit = creditService.create(Credit.builder()
                            .creditLimit(totalPrice)
                            .totalDebt(creditActivityType.equals(CreditActivityType.DEBT) ?
                                    totalPrice : 0)
                            .creditType(CreditType.MERCHANT_CREDIT)
                            .customer(order.getBuyer()).merchant(order.getSeller()).build());
                }

                activityService.create(activityPopulator(order.getBuyer(), order.getSeller(), writableOrder.getPaidPrice(), credit.getTotalDebt(), credit.getCreditLimit() - credit.getTotalDebt(), credit.getCreditLimit(), writableOrder.getPaymentType(), ActivityType.MERCHANT_CREDIT));

            }
        } else if (OrderStatus.CANCELLED.equals(writableOrder.getStatus())) {
            Obligation obligation = obligationService.findByUser(order.getSeller());
            if (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType())) {
                obligation.setReceivable(obligation.getReceivable() - order.getCommission());
            } else {
                obligation.setDebt(obligation.getDebt() - order.getCommission());
            }
            obligationService.update(obligation.getUuid().toString(), obligation);
            obligationActivityService.create(obligationActivityPopulator(obligation, order, CreditActivityType.CREDIT));
            Credit credit = PaymentOption.MERCHANT_CREDIT.equals(order.getPaymentType()) ? creditService.findByCustomerAndMerchant(order.getBuyer(), order.getSeller())
                    .orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND + "credit.user", ""))
                    : (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) ? creditService.findSystemCreditByUser(order.getBuyer()) : null);
            if (credit != null) {
                credit.setTotalDebt(credit.getTotalDebt() - order.getTotalPrice());

                activityService.create(activityPopulator(order.getBuyer(), order.getSeller(), order.getTotalPrice(), credit.getTotalDebt(), credit.getCreditLimit() - credit.getTotalDebt(), credit.getCreditLimit(), null, ActivityType.ORDER_CANCEL));
                creditService.update(credit.getUuid().toString(), credit);
            }
        }

        order.setStatus(writableOrder.getStatus());

        return OrderMapper.orderToReadableOrder(orderService.update(order.getUuid().toString(), order));
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
        double currentDebt = 0;
        for (WritableConfirmOrderItem writableConfirmOrderItem : writableConfirmOrder.getItems()) {
            OrderItem orderItem = orderItemService.findByUUID(writableConfirmOrderItem.getId());
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
        order.setCommission(updatedItems.stream().mapToDouble(OrderItem::getCommission).sum());
        double orderOldTotalPrice = order.getTotalPrice();
        order.setTotalPrice(updatedItems.stream().mapToDouble(OrderItem::getTotalPrice).sum());
        if (orderOldTotalPrice != order.getTotalPrice()) {
            Obligation obligation = obligationService.findByUser(order.getSeller());
            obligationService.update(obligation.getUuid().toString(), calculateObligation(order, obligation, orderOldTotalPrice));
        }
        Credit credit = order.getPaymentType().equals(PaymentOption.MERCHANT_CREDIT)
                ? creditService.findByCustomerAndMerchant(order.getBuyer(), order.getSeller())
                .orElseThrow(() -> new BadRequestException("You have not credit from this merchant"))
                : creditService.findSystemCreditByUser(order.getBuyer());
        if (!PaymentOption.COD.equals(order.getPaymentType())) {
            credit.setTotalDebt(credit.getTotalDebt() + order.getTotalPrice());
            currentDebt = order.getTotalPrice();
            creditService.update(credit.getUuid().toString(), credit);
        }

        activityService.create(activityPopulator(order.getBuyer(), order.getSeller(), order.getTotalPrice(), currentDebt, 0, credit.getCreditLimit(), PaymentType.fromValue(order.getPaymentType().toString()), ActivityType.ORDER));
        return OrderMapper.orderToReadableOrder(orderService.update(order.getUuid().toString(), order));
    }

    @Override
    public List<ReadableOrder> checkoutCart(User user, Set<CartItemHolder> cartItemHolderList, WritableCheckout writableCheckout) {
        {
            List<Order> orders = new ArrayList<>();
            List<OrderItem> orderItems = new ArrayList<>();
            List<Obligation> obligations = new ArrayList<>();
            List<ObligationActivity> obligationActivities = new ArrayList<>();
            for (CartItemHolder cartItemHolder : cartItemHolderList) {
                Order order = ordersPopulator(cartItemHolder, user);
                order.setOrderItems(orderItemsPopulator(cartItemHolder.getCartItems(), order));
                orders.add(order);
                Obligation obligation = calculateObligation(order, obligationService.findByUser(order.getSeller()), 0);
                obligations.add(obligation);
                CreditActivityType obligationType = PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) ? CreditActivityType.CREDIT : CreditActivityType.DEBT;
                obligationActivities.add(obligationActivityPopulator(obligation, order, obligationType));
                orderItems.addAll(order.getOrderItems());

            }
            orderService.createAll(orders);
            orderItemService.saveAll(orderItems);
            obligationService.createAll(obligations);
            obligationActivityService.saveAll(obligationActivities);

            return orders.stream().map(OrderMapper::orderToReadableOrder).collect(Collectors.toList());
        }
    }

    private Order ordersPopulator(CartItemHolder cartItemHolder, User user) {
        double orderTotalPrice = cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getTotalPrice).sum();
        double discountedTotalPrice = cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getDiscountedTotalPrice).sum();
        if (discountedTotalPrice == 0) {
            discountedTotalPrice = orderTotalPrice;
        }
        Order order = new Order();
        order.setBuyer(user);
        order.setSeller(userService.findByUUID(cartItemHolder.getSellerId()));
        order.setOrderDate(new Date());
        order.setTotalPrice(discountedTotalPrice);
        order.setPaymentType(cartItemHolder.getPaymentOption());
        order.setStatus(OrderStatus.NEW);
        return order;
    }

    private Obligation calculateObligation(Order order, Obligation obligation, double oldCommission) {
        double commission = Math.abs(order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum() - oldCommission);
        boolean paymentType = order.getPaymentType().equals(PaymentOption.COD) || order.getPaymentType().equals(PaymentOption.MERCHANT_CREDIT);
        obligation.setDebt(paymentType ? obligation.getDebt() + commission : obligation.getDebt());
        obligation.setReceivable(paymentType ? obligation.getReceivable() : obligation.getReceivable() + (order.getTotalPrice() - commission));
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
        orderItem.setTotalPrice(orderItem.getPrice() * writableConfirmOrderItem.getQuantity());
        orderItem.setCommission(orderItem.getTotalPrice() * orderItem.getProductSpecify().getCommission());
        return orderItem;
    }

    private CreditActivity creditActivityPopulator(Order order, Credit credit, double totalPrice, CreditActivityType creditActivityType) {
        CreditActivity creditActivity = new CreditActivity();
        creditActivity.setCreditActivityType(creditActivityType);
        creditActivity.setPriceValue(totalPrice);
        creditActivity.setCreditLimit(credit.getCreditLimit());
        creditActivity.setCurrentDebt(credit.getTotalDebt());
        creditActivity.setCredit(credit);
        creditActivity.setMerchant(order.getSeller());
        creditActivity.setCustomer(order.getBuyer());
        creditActivity.setOrder(order);
        return creditActivity;
    }

    private ObligationActivity obligationActivityPopulator(Obligation obligation, Order order, CreditActivityType creditActivityType) {
        ObligationActivity obligationActivity = new ObligationActivity();
        double commission = order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum();
        boolean paymentType = order.getPaymentType().equals(PaymentOption.COD) || order.getPaymentType().equals(PaymentOption.MERCHANT_CREDIT);
        double price = paymentType ? commission : order.getTotalPrice() - commission;
        obligationActivity.setObligation(obligation);
        obligationActivity.setPriceValue(price);
        obligationActivity.setCreditActivityType(creditActivityType);
        obligationActivity.setDate(new Date());
        return obligationActivity;
    }

    private Activity activityPopulator(User customer, User merchant, double price, double currentDebt, double currentReceivable, double creditLimit, PaymentType paymentType, ActivityType activityType) {
        return Activity.builder().activityType(activityType).paymentType(paymentType)
                .customer(customer).merchant(merchant)
                .price(price).creditLimit(creditLimit).currentDebt(currentDebt).currentReceivable(currentReceivable)
                .build();
    }
}
