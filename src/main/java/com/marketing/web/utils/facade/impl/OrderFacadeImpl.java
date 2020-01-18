package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.enums.CartStatus;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.cart.CartService;
import com.marketing.web.services.credit.SystemCreditService;
import com.marketing.web.services.invoice.InvoiceService;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.order.OrderItemService;
import com.marketing.web.services.order.OrderService;
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
    private CartItemService cartItemService;

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

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Order order) {
        if (OrderStatus.FNS.equals(order.getStatus())) {
            throw new BadRequestException("Finished order can not be updated");
        }
        order.setStatus(writableOrder.getStatus());
        order.setWaybillDate(writableOrder.getWaybillDate());

        if (writableOrder.getStatus().equals(OrderStatus.FNS)){
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

                invoice.setUnPaidPrice((order.getDiscountedTotalPrice()-discount)-paidPrice);

                obligation.setDebt(commission);
                obligation.setReceivable(0);
            }else {
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

            if (invoice.getUnPaidPrice() > 0){
                Obligation buyerObligation = new Obligation();
                buyerObligation.setDebt(invoice.getUnPaidPrice());
                buyerObligation.setReceivable(0);
                buyerObligation.setUser(order.getBuyer());
                obligationService.create(buyerObligation);
            }
        }
        return OrderMapper.orderToReadableOrder(orderService.update(order.getUuid().toString(),order));


    }

    @Override
    public List<ReadableOrder> checkoutCart(User user, Cart cart, Long sellerId) {
        {
            List<CartItem> cartItemList;
            if (sellerId > 0){
                cartItemList = cart.getItems().stream().filter(cartItem -> cartItem.getProduct().getUser().getId().equals(sellerId)).collect(Collectors.toList());
            }else {
                cartItemList = cart.getItems();
            }
            List<Order> orders = ordersPopulator(cart, cartItemList, user);

            if (cart.getPaymentOption().equals(PaymentOption.CRD)){
                double ordersTotalPrice = orders.stream().mapToDouble(Order::getDiscountedTotalPrice).sum();
                SystemCredit systemCredit = systemCreditService.findByUser(user.getId());
                systemCredit.setTotalDebt(systemCredit.getTotalDebt() + ordersTotalPrice);
                if (systemCredit.getTotalDebt() > systemCredit.getCreditLimit()) {
                    throw new BadRequestException("Not enough systemCredit limit");
                }
                systemCreditService.update(systemCredit.getUuid().toString(), systemCredit);
            }

            orderService.createAll(orders);
            cart.setPaymentOption(null);
            cart.setCartStatus(CartStatus.NEW);
            cartService.update(cart.getId(), cart);
            cartItemService.deleteAll(cartItemList);

            List<OrderItem> orderItems = orderItemsPopulator(cartItemList, orders);
            orderItemService.createAll(orderItems);
            return orders.stream().map(OrderMapper::orderToReadableOrder).collect(Collectors.toList());
        }
    }

    private List<Order> ordersPopulator(Cart cart, List<CartItem> cartItems, User user) {
        List<Order> orders = new ArrayList<>();
        List<User> sellers = cartItems.stream().map(cartItem -> cartItem.getProduct().getUser())
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(User::getId))), ArrayList::new));
        for (User seller : sellers){
            double orderTotalPrice = cartItems.stream().filter(cartItem -> cartItem.getProduct().getUser().getId().equals(seller.getId()))
                    .mapToDouble(CartItem::getTotalPrice).sum();
            double discountedTotalPrice = cartItems.stream().filter(cartItem -> cartItem.getProduct().getUser().getId().equals(seller.getId()))
                    .mapToDouble(CartItem::getDiscountedTotalPrice).sum();
            if (discountedTotalPrice == 0){
                discountedTotalPrice = orderTotalPrice;
            }
            Order order = new Order();
            order.setBuyer(user);
            order.setSeller(seller);
            order.setOrderDate(new Date());
            order.setTotalPrice(orderTotalPrice);
            order.setDiscountedTotalPrice(discountedTotalPrice);
            order.setPaymentType(cart.getPaymentOption());
            if(cart.getPaymentOption().equals(PaymentOption.CRD)){
                order.setStatus(OrderStatus.PAD);
            }else {
                order.setStatus(OrderStatus.NEW);
            }
            orders.add(order);
        }
        return orders;
    }

    private List<OrderItem> orderItemsPopulator(List<CartItem> cartItems, List<Order> orders) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems){
            OrderItem orderItem = OrderMapper.cartItemToOrderItem(cartItem);
            Optional<Order> optionalOrder = orders.stream().filter(order -> order.getSeller().getId().equals(orderItem.getSeller().getId())).findFirst();
            optionalOrder.ifPresent(order -> {
                orderItem.setOrder(order);
                order.addOrderItem(orderItem);
            });
            orderItems.add(orderItem);
        }
        return orderItems;
    }
}
