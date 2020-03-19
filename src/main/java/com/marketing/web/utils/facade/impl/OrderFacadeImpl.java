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
import com.marketing.web.services.credit.CreditActivityService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.invoice.ObligationActivityService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.order.OrderItemService;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service(value = "orderFacade")
public class OrderFacadeImpl implements OrderFacade {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private ObligationActivityService obligationActivityService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private CreditActivityService creditActivityService;

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Order order) {
        if (OrderStatus.CONFIRMED.equals(order.getStatus())
                && OrderStatus.FINISHED.equals(writableOrder.getStatus())
                && writableOrder.getWaybillDate() != null) {

            order.setWaybillDate(writableOrder.getWaybillDate());

            if ((PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) || PaymentOption.MERCHANT_CREDIT.equals(order.getPaymentType())) && writableOrder.getPaidPrice() > 0) {
                Optional<Credit> optionalCredit = creditService.findByCustomerAndMerchant(order.getBuyer(), order.getSeller());
                Credit credit;
                if (optionalCredit.isPresent()) {
                    credit = optionalCredit.get();
                    credit.setTotalDebt(credit.getTotalDebt() - writableOrder.getPaidPrice());
                    creditService.update(credit.getUuid().toString(), credit);
                } else {
                    credit = creditService.create(Credit.builder().creditLimit(writableOrder.getPaidPrice()).customer(order.getBuyer()).merchant(order.getSeller()).creditType(CreditType.MERCHANT_CREDIT).totalDebt(0).build());
                }

                CreditActivity creditActivity = creditActivityPopulator(order,credit,writableOrder.getPaidPrice(),CreditActivityType.CREDIT);
                creditActivityService.create(creditActivity);

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
                CreditActivity creditActivity = creditActivityPopulator(order,credit,totalPrice, creditActivityType);
                creditActivityService.create(creditActivity);
            }
        } else if (OrderStatus.CANCELLED.equals(writableOrder.getStatus())) {
                Obligation obligation = obligationService.findByUser(order.getSeller());
                if (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType())) {
                    obligation.setReceivable(obligation.getReceivable() - order.getTotalPrice());
                } else {
                    obligation.setDebt(obligation.getDebt() - order.getTotalPrice());
                }
                obligationService.update(obligation.getUuid().toString(), obligation);
                obligationActivityService.create(obligationActivityPopulator(obligation, order , CreditActivityType.CREDIT));
                Credit credit = PaymentOption.MERCHANT_CREDIT.equals(order.getPaymentType()) ? creditService.findByCustomerAndMerchant(order.getBuyer(), order.getSeller())
                        .orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",""))
                        : (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) ? creditService.findSystemCreditByUser(order.getBuyer()) : null);
                if (credit != null) {
                    credit.setTotalDebt(credit.getTotalDebt() - order.getTotalPrice());
                    creditActivityService.deleteByOrder(order);
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
        List<OrderItem> removedItems = new ArrayList<>();
        List<OrderItem> updatedItems = new ArrayList<>();
        for (WritableConfirmOrderItem writableConfirmOrderItem : writableConfirmOrder.getItems()) {
            OrderItem orderItem = orderItemService.findByUUIDAndOrder(writableConfirmOrderItem.getId(), order);
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
            if (!PaymentOption.COD.equals(order.getPaymentType())) {
                Optional<Credit> creditOptional = creditActivityService.findAllByOrder(order).stream().findAny().map(CreditActivity::getCredit);
                if (creditOptional.isPresent()) {
                    Credit credit = creditOptional.get();
                    CreditActivity creditActivity = new CreditActivity();
                    double activityPrice = Math.abs(order.getTotalPrice() - orderOldTotalPrice);
                    if (order.getTotalPrice() > orderOldTotalPrice) {
                        creditActivity.setCreditActivityType(CreditActivityType.CREDIT);
                        credit.setTotalDebt(credit.getTotalDebt() + (order.getTotalPrice() - orderOldTotalPrice));
                    } else if (order.getTotalPrice() < orderOldTotalPrice){
                        creditActivity.setCreditActivityType(CreditActivityType.DEBT);
                        credit.setTotalDebt(credit.getTotalDebt() - activityPrice);
                    }
                    creditActivity.setCustomer(order.getBuyer());
                    if (PaymentOption.MERCHANT_CREDIT.equals(order.getPaymentType())) {
                        creditActivity.setMerchant(order.getBuyer());
                    }
                    creditActivity.setCredit(credit);
                    creditActivity.setOrder(order);
                    creditActivity.setPriceValue(activityPrice);
                    creditActivityService.create(creditActivity);
                    creditService.update(credit.getUuid().toString(), credit);
                }
            }


            Obligation obligation = calculateObligation(order, obligationService.findByUser(order.getSeller()));
            CreditActivityType obligationActivityType;
            if (PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType())) {
                obligation.setReceivable(obligation.getReceivable() - orderOldTotalPrice);
                obligationActivityType = CreditActivityType.CREDIT;
            } else {
                obligationActivityType = CreditActivityType.DEBT;
                obligation.setDebt(obligation.getDebt() - orderOldTotalPrice);
            }
            obligationService.update(obligation.getUuid().toString(), obligation);
            obligationActivityService.create(obligationActivityPopulator(obligation, order, obligationActivityType));

            return OrderMapper.orderToReadableOrder(orderService.update(order.getUuid().toString(), order));
    }

    @Override
    public List<ReadableOrder> checkoutCart(User user, Set<CartItemHolder> cartItemHolderList, WritableCheckout writableCheckout) {
        {

            List<Order> orders = new ArrayList<>();
            List<OrderItem> orderItems = new ArrayList<>();
            List<Obligation> obligations = new ArrayList<>();
            List<CreditActivity> creditActivities = new ArrayList<>();
            List<ObligationActivity> obligationActivities = new ArrayList<>();
            List<Credit> credits = new ArrayList<>();
            for (CartItemHolder cartItemHolder : cartItemHolderList) {
                PaymentOption paymentOption = cartItemHolder.getPaymentOption();
                Order order = ordersPopulator(cartItemHolder, user);
                order.setOrderItems(orderItemsPopulator(cartItemHolder.getCartItems(), order));
                orders.add(order);
                Obligation obligation = calculateObligation(order, obligationService.findByUser(order.getSeller()));
                obligations.add(calculateObligation(order, obligation));
                CreditActivityType obligationType = PaymentOption.SYSTEM_CREDIT.equals(order.getPaymentType()) ? CreditActivityType.CREDIT : CreditActivityType.DEBT;
                obligationActivities.add(obligationActivityPopulator(obligation, order, obligationType));
                orderItems.addAll(order.getOrderItems());

                if (PaymentOption.MERCHANT_CREDIT.equals(paymentOption) || PaymentOption.SYSTEM_CREDIT.equals(paymentOption)) {
                    Credit credit = paymentOption.equals(PaymentOption.MERCHANT_CREDIT)
                            ? creditService.findByCustomerAndMerchant(user, userService.findByUUID(cartItemHolder.getSellerId()))
                            .orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"credit.user",""))
                            :  creditService.findSystemCreditByUser(user);
                    credit.setTotalDebt(cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getDiscountedTotalPrice).sum());
                    if (credit.getTotalDebt() > credit.getCreditLimit()) {
                        throw new BadRequestException("Not enough credit limit");
                    }
                    CreditActivity creditActivity = new CreditActivity();
                    creditActivity.setCreditActivityType(CreditActivityType.DEBT);
                    creditActivity.setCustomer(credit.getCustomer());
                    creditActivity.setCredit(credit);
                    if (CreditType.MERCHANT_CREDIT.equals(credit.getCreditType())) {
                        creditActivity.setMerchant(credit.getMerchant());
                    }
                    creditActivity.setPriceValue(order.getTotalPrice());
                    creditActivity.setOrder(order);
                    credits.add(credit);
                    creditActivities.add(creditActivity);
                }

            }

            orderService.createAll(orders);
            orderItemService.saveAll(orderItems);
            obligationService.createAll(obligations);
            obligationActivityService.saveAll(obligationActivities);
            creditService.saveAll(credits);
            creditActivityService.saveAll(creditActivities);

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

    private Obligation calculateObligation(Order order, Obligation obligation) {
        double commission = order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum();
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
}
