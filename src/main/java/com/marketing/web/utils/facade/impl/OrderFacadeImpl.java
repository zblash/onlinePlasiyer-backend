package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.cart.WritableCheckout;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.enums.CartStatus;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartItemHolderService;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.cart.CartService;
import com.marketing.web.services.credit.SystemCreditService;
import com.marketing.web.services.credit.UsersCreditService;
import com.marketing.web.services.invoice.InvoiceService;
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

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

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
    private InvoiceService invoiceService;

    @Autowired
    private ObligationService obligationService;

    @Autowired
    private SystemCreditService systemCreditService;

    @Autowired
    private UsersCreditService usersCreditService;

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Order order) {
        if (OrderStatus.FNS.equals(order.getStatus())) {
            throw new BadRequestException("Finished order can not be updated");
        }
        order.setStatus(writableOrder.getStatus());
        order.setWaybillDate(writableOrder.getWaybillDate());

        if (writableOrder.getStatus().equals(OrderStatus.FNS)) {
            double commission = order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum();

            order.setCommission(commission);

            Obligation obligation = new Obligation();

            Invoice invoice = new Invoice();
            invoice.setBuyer(order.getBuyer());
            invoice.setSeller(order.getSeller());

            if (order.getPaymentType().equals(PaymentOption.COD)) {
                double discount = Optional.of(writableOrder.getDiscount()).orElse(0.0);
                double paidPrice = Optional.of(writableOrder.getPaidPrice()).orElse(order.getDiscountedTotalPrice());
                paidPrice = Math.min(paidPrice, order.getDiscountedTotalPrice());
                discount = discount >= order.getDiscountedTotalPrice() || discount < 0 ? 0.0 : discount;
                invoice.setDiscount(discount);
                invoice.setPaidPrice(paidPrice);

                invoice.setUnPaidPrice((order.getDiscountedTotalPrice() - discount) - paidPrice);

                obligation.setDebt(commission);
                obligation.setReceivable(0);
            } else {
                invoice.setDiscount(0);
                invoice.setPaidPrice(order.getDiscountedTotalPrice());
                invoice.setUnPaidPrice(0);

                obligation.setDebt(0);
                obligation.setReceivable(order.getDiscountedTotalPrice() - commission);
            }

            invoice.setTotalPrice(order.getDiscountedTotalPrice());
            invoice.setOrder(order);
            invoice.setBuyer(order.getBuyer());
            invoice.setSeller(order.getSeller());

            invoiceService.create(invoice);

            obligation.setUser(order.getSeller());
            obligationService.create(obligation);

            if (invoice.getUnPaidPrice() > 0) {
                Obligation buyerObligation = new Obligation();
                buyerObligation.setDebt(invoice.getUnPaidPrice());
                buyerObligation.setReceivable(0);
                buyerObligation.setUser(order.getBuyer());
                obligationService.create(buyerObligation);
            }
        }
        return OrderMapper.orderToReadableOrder(orderService.update(order.getUuid().toString(), order));


    }

    @Override
    public List<ReadableOrder> checkoutCart(User user, Cart cart, WritableCheckout writableCheckout) {
        {

            Set<CartItemHolder> cartItemHolderList = cart.getItems().stream()
                    .filter(cartItemHolder -> writableCheckout.getSellerIdList().contains(cartItemHolder.getUuid().toString()))
                    .collect(Collectors.toSet());

            List<Order> orders = ordersPopulator(cartItemHolderList, user);


                cartItemHolderList.forEach(cartItemHolder -> {
                    PaymentOption paymentOption = cartItemHolder.getPaymentOption();
                    double ordersTotalPrice = cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getDiscountedTotalPrice).sum();
                    switch (paymentOption) {
                        case SCRD:
                            SystemCredit systemCredit = systemCreditService.findByUser(user.getId());
                            systemCredit.setTotalDebt(systemCredit.getTotalDebt() + ordersTotalPrice);
                            if (systemCredit.getTotalDebt() > systemCredit.getCreditLimit()) {
                                throw new BadRequestException("Not enough systemCredit limit");
                            }
                            systemCreditService.update(systemCredit.getUuid().toString(), systemCredit);
                            break;
                        case MCRD:
                            UsersCredit usersCredit = usersCreditService.findByCustomerAndMerchant(user, userService.findByUUID(cartItemHolder.getSellerId()));
                            usersCredit.setTotalDebt(ordersTotalPrice);
                            if (usersCredit.getTotalDebt() > usersCredit.getCreditLimit()) {
                                throw new BadRequestException("Not enough credit limit");
                            }
                            usersCreditService.update(usersCredit.getUuid().toString(),usersCredit);
                    }

                });


            cart.setCartStatus(CartStatus.NEW);
            cartService.update(cart.getId(), cart);
            cartItemHolderService.deleteAll(cartItemHolderList);

            return orders.stream().map(OrderMapper::orderToReadableOrder).collect(Collectors.toList());
        }
    }

    private List<Order> ordersPopulator(Set<CartItemHolder> cartItemHolders, User user) {
        List<Order> orders = new ArrayList<>();
        for (CartItemHolder cartItemHolder : cartItemHolders) {
            double orderTotalPrice = cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getTotalPrice).sum();
            double discountedTotalPrice = cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getDiscountedTotalPrice).sum();
            if (discountedTotalPrice == 0) {
                discountedTotalPrice = orderTotalPrice;
            }
            Order order = new Order();
            order.setBuyer(user);
            order.setSeller(userService.findByUUID(cartItemHolder.getSellerId()));
            order.setOrderDate(new Date());
            order.setTotalPrice(orderTotalPrice);
            order.setDiscountedTotalPrice(discountedTotalPrice);
            order.setPaymentType(cartItemHolder.getPaymentOption());
            if (cartItemHolder.getPaymentOption().equals(PaymentOption.SCRD) || cartItemHolder.getPaymentOption().equals(PaymentOption.MCRD)) {
                order.setStatus(OrderStatus.PAD);
            } else {
                order.setStatus(OrderStatus.NEW);
            }

            orderItemsPopulator(cartItemHolder.getCartItems(), order);
            obligationPopulator(order);
            orders.add(order);
        }
        return orderService.createAll(orders);
    }

    private void obligationPopulator(Order order) {
       Obligation obligation = new Obligation();
        double commission = order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum();
        boolean paymentType = order.getPaymentType().equals(PaymentOption.COD) || order.getPaymentType().equals(PaymentOption.MCRD);
        obligation.setDebt(paymentType ? commission : 0);
        obligation.setReceivable(paymentType ? 0 : order.getDiscountedTotalPrice() - commission);
        obligation.setUser(order.getSeller());
        obligation.setOrder(order);
        obligationService.create(obligation);
    }

    private List<OrderItem> orderItemsPopulator(Set<CartItem> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderMapper.cartItemToOrderItem(cartItem);
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            orderItems.add(orderItem);
        }

        return orderItemService.createAll(orderItems);
    }
}
