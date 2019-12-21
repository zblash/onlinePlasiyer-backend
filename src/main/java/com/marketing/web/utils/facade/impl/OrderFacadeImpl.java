package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.enums.CartStatus;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.cart.CartItemServiceImpl;
import com.marketing.web.services.cart.CartService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.invoice.InvoiceService;
import com.marketing.web.services.invoice.InvoiceServiceImpl;
import com.marketing.web.services.invoice.ObligationService;
import com.marketing.web.services.order.OrderItemService;
import com.marketing.web.services.order.OrderItemServiceImpl;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.order.OrderServiceImpl;
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
    private CreditService creditService;

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, Order order) {
        order.setStatus(writableOrder.getStatus());
        order.setWaybillDate(writableOrder.getWaybillDate());

        if (writableOrder.getStatus().equals(OrderStatus.FNS)){

            double commission = order.getTotalPrice() * 0.01;
            order.setCommission(commission);

            Obligation obligation = new Obligation();

            Invoice invoice = new Invoice();
            invoice.setBuyer(order.getBuyer());
            invoice.setSeller(order.getSeller());

            if (order.getPaymentType().equals(PaymentOption.COD)) {
                double discount = Optional.of(writableOrder.getDiscount()).orElse(0.0);
                double paidPrice = Optional.of(writableOrder.getPaidPrice()).orElse(order.getTotalPrice());
                paidPrice = paidPrice > order.getTotalPrice() ? order.getTotalPrice() : paidPrice;
                discount = discount >= order.getTotalPrice() || discount < 0 ? 0.0 : discount;
                invoice.setDiscount(discount);
                invoice.setPaidPrice(paidPrice);
                invoice.setUnPaidPrice((order.getTotalPrice()-discount)-paidPrice);

                obligation.setDebt(commission);
                obligation.setReceivable(0);
            }else {
                invoice.setDiscount(0);
                invoice.setPaidPrice(order.getTotalPrice());
                invoice.setUnPaidPrice(0);

                obligation.setDebt(0);
                obligation.setReceivable(order.getTotalPrice() - commission);
            }

            invoice.setTotalPrice(order.getTotalPrice());
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
    public List<ReadableOrder> checkoutCart(User user, Cart cart, List<CartItem> cartItems) {
        {
            List<Order> orders = new ArrayList<>();
            double ordersTotalPrice = 0;
            List<User> sellers = cartItems.stream().map(cartItem -> cartItem.getProduct().getUser())
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(User::getId))), ArrayList::new));
            for (User seller : sellers){
                double orderTotalPrice = cartItems.stream().filter(cartItem -> cartItem.getProduct().getUser().getId().equals(seller.getId()))
                        .mapToDouble(CartItem::getTotalPrice).sum();
                Order order = new Order();
                order.setBuyer(user);
                order.setSeller(seller);
                order.setOrderDate(new Date());
                order.setTotalPrice(orderTotalPrice);
                order.setPaymentType(cart.getPaymentOption());
                if(cart.getPaymentOption().equals(PaymentOption.CC) ||
                        cart.getPaymentOption().equals(PaymentOption.CRD)){
                    order.setStatus(OrderStatus.PAD);
                }else {
                    order.setStatus(OrderStatus.NEW);
                }
                ordersTotalPrice += orderTotalPrice;
                orders.add(order);
            }
            if (cart.getPaymentOption().equals(PaymentOption.CRD)){
                Credit credit = creditService.findByUser(user.getId());
                credit.setTotalDebt(credit.getTotalDebt() + ordersTotalPrice);
                if (credit.getTotalDebt() > credit.getCreditLimit()) {
                    throw new BadRequestException("Not enough credit limit");
                }
                creditService.update(credit.getUuid().toString(), credit);
            }

            orderService.createAll(orders);
            cartItemService.deleteAll(cart);
            cart.setPaymentOption(null);
            cart.setCartStatus(CartStatus.NEW);
            cartService.update(cart.getId(), cart);

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
            orderItemService.createAll(orderItems);
            return orders.stream().map(OrderMapper::orderToReadableOrder).collect(Collectors.toList());
        }
    }

}
