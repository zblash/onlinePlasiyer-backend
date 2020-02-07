package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.cart.WritableCheckout;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableConfirmOrder;
import com.marketing.web.dtos.order.WritableConfirmOrderItem;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.enums.CartStatus;
import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartItemHolderService;
import com.marketing.web.services.cart.CartService;
import com.marketing.web.services.credit.CreditActivityService;
import com.marketing.web.services.credit.CreditService;
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
    private CartItemHolderService cartItemHolderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private CreditActivityService creditActivityService;

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Order order) {

        if (OrderStatus.CNFRM.equals(order.getStatus())
                && OrderStatus.FNS.equals(writableOrder.getStatus())
                && writableOrder.getWaybillDate() != null) {

            order.setWaybillDate(writableOrder.getWaybillDate());

            if (PaymentOption.MCRD.equals(order.getPaymentType()) && writableOrder.getPaidPrice() > 0) {
                Credit credit = creditService.findByCustomerAndMerchant(order.getBuyer(), order.getSeller());
                credit.setTotalDebt(credit.getTotalDebt() - writableOrder.getPaidPrice());
                CreditActivity creditActivity = new CreditActivity();
                creditActivity.setCreditActivityType(CreditActivityType.CRDT);
                creditActivity.setPriceValue(writableOrder.getPaidPrice());
                creditActivity.setCredit(credit);
                creditActivity.setOrder(order);
                creditService.update(credit.getUuid().toString(), credit);
                creditActivityService.create(creditActivity);
            } else if (PaymentOption.COD.equals(order.getPaymentType()) && writableOrder.getPaidPrice() > order.getTotalPrice()) {
                double totalPrice = writableOrder.getPaidPrice() - order.getTotalPrice();
                Credit credit = creditService.create(Credit.builder()
                        .creditLimit(totalPrice)
                        .customer(order.getBuyer()).merchant(order.getSeller()).totalDebt(0).build());
                CreditActivity creditActivity = new CreditActivity();
                creditActivity.setCreditActivityType(CreditActivityType.CRDT);
                creditActivity.setPriceValue(totalPrice);
                creditActivity.setCredit(credit);
                creditActivity.setOrder(order);
                creditActivityService.create(creditActivity);
            }
        } else {
            order.setStatus(writableOrder.getStatus());
            if (OrderStatus.CNCL.equals(writableOrder.getStatus())) {
                obligationService.delete(obligationService.findByOrder(order));
                if (PaymentOption.MCRD.equals(order.getPaymentType()) || PaymentOption.SCRD.equals(order.getPaymentType())) {
                    creditActivityService.deleteByOrder(order);
                }
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
            order.setStatus(OrderStatus.CNFRM);
            order.setCommission(updatedItems.stream().mapToDouble(OrderItem::getCommission).sum());
            order.setTotalPrice(updatedItems.stream().mapToDouble(OrderItem::getTotalPrice).sum());

            obligationService.update(obligationService.findByOrder(order).getUuid().toString(), obligationPopulator(order));

            return OrderMapper.orderToReadableOrder(orderService.update(order.getUuid().toString(), order));
    }

    @Override
    public List<ReadableOrder> checkoutCart(User user, Cart cart, WritableCheckout writableCheckout) {
        {

            List<CartItemHolder> cartItemHolderList = cart.getItems().stream()
                    .filter(cartItemHolder -> writableCheckout.getSellerIdList().contains(cartItemHolder.getUuid().toString()))
                    .collect(Collectors.toList());

            List<Order> orders = new ArrayList<>();
            List<OrderItem> orderItems = new ArrayList<>();
            List<Obligation> obligations = new ArrayList<>();
            CreditActivity creditActivity = new CreditActivity();
            Credit credit = null;
            for (CartItemHolder cartItemHolder : cartItemHolderList) {
                PaymentOption paymentOption = cartItemHolder.getPaymentOption();
                double ordersTotalPrice = cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getDiscountedTotalPrice).sum();

                Order order = ordersPopulator(cartItemHolder, user);
                order.setOrderItems(orderItemsPopulator(cartItemHolder.getCartItems(), order));
                orders.add(order);
                obligations.add(obligationPopulator(order));
                orderItems.addAll(order.getOrderItems());

                if (PaymentOption.MCRD.equals(paymentOption) || PaymentOption.SCRD.equals(paymentOption)) {
                    credit = paymentOption.equals(PaymentOption.MCRD)
                            ? creditService.findByCustomerAndMerchant(user, userService.findByUUID(cartItemHolder.getSellerId()))
                            :  creditService.findSystemCreditByUser(user);
                    credit.setTotalDebt(ordersTotalPrice);
                    if (credit.getTotalDebt() > credit.getCreditLimit()) {
                        throw new BadRequestException("Not enough credit limit");
                    }
                    creditActivity.setCreditActivityType(CreditActivityType.DEBT);
                    creditActivity.setPriceValue(order.getTotalPrice());
                    creditActivity.setCredit(credit);
                    creditActivity.setOrder(order);
                }
            }

            cart.setCartStatus(CartStatus.NEW);
            cartService.update(cart.getId(), cart);
            cartItemHolderService.deleteAll(new HashSet<>(cartItemHolderList));

            orderService.createAll(orders);
            orderItemService.saveAll(orderItems);
            obligationService.createAll(obligations);
            if (credit != null) {
                creditService.update(credit.getUuid().toString(), credit);
                creditActivityService.create(creditActivity);
            }
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

    private Obligation obligationPopulator(Order order) {
        Obligation obligation = new Obligation();
        double commission = order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum();
        boolean paymentType = order.getPaymentType().equals(PaymentOption.COD) || order.getPaymentType().equals(PaymentOption.MCRD);
        obligation.setDebt(paymentType ? commission : 0);
        obligation.setReceivable(paymentType ? 0 : order.getTotalPrice() - commission);
        obligation.setUser(order.getSeller());
        obligation.setOrder(order);
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
}
