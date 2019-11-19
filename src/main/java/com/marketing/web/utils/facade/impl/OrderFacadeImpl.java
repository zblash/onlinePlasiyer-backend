package com.marketing.web.utils.facade.impl;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.cart.CartItemServiceImpl;
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
    private OrderItemService orderItemService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ObligationService obligationService;

    @Override
    public ReadableOrder saveOrder(WritableOrder writableOrder, String uuid, User seller) {
        Order order = orderService.findByUuidAndUser(uuid,seller);
        order.setStatus(writableOrder.getStatus());
        order.setWaybillDate(writableOrder.getWaybillDate());

        if (writableOrder.getStatus().equals(OrderStatus.FNS) || order.getStatus().equals(OrderStatus.PAD)){

            double commission = order.getTotalPrice() * 0.01;
            order.setCommission(commission);

            Obligation obligation = new Obligation();
            obligation.setDebt(commission);
            obligation.setReceivable(0);
            obligation.setUser(seller);
            obligationService.create(obligation);

            Invoice invoice = new Invoice();
            invoice.setBuyer(order.getBuyer());
            invoice.setSeller(order.getSeller());
            double discount = Optional.of(writableOrder.getDiscount()).orElse(0.0);
            double paidPrice = Optional.of(writableOrder.getPaidPrice()).orElse(order.getTotalPrice());
            paidPrice = paidPrice > order.getTotalPrice() ? order.getTotalPrice() : paidPrice;
            discount = discount >= order.getTotalPrice() || discount < 0 ? 0.0 : discount;
            invoice.setDiscount(discount);
            invoice.setPaidPrice(paidPrice);
            invoice.setUnPaidPrice((order.getTotalPrice()-discount)-paidPrice);
            invoice.setTotalPrice(order.getTotalPrice());
            invoice.setOrder(order);
            invoice.setBuyer(order.getBuyer());
            invoice.setSeller(order.getSeller());
            invoiceService.create(invoice);
        }
        return OrderMapper.orderToReadableOrder(orderService.update(order.getUuid().toString(),order));


    }

    @Override
    public List<ReadableOrder> checkoutCart(User user, Cart cart, List<CartItem> cartItems) {
        {
            List<Order> orders = new ArrayList<>();
            List<User> sellers = cartItems.stream().map(cartItem -> cartItem.getProduct().getUser())
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(User::getId))), ArrayList::new));
            for (User seller : sellers){
                double orderTotalPrice = cartItems.stream().filter(cartItem -> cartItem.getProduct().getUser().getId().equals(seller.getId()))
                        .mapToDouble(CartItem::getTotalPrice).sum();
                Order order = new Order();
                order.setBuyer(user);
                order.setSeller(seller);
                order.setOrderDate(new Date());
                order.setStatus(OrderStatus.NEW);
                order.setTotalPrice(orderTotalPrice);
                orders.add(order);
            }
            orderService.createAll(orders);

            cartItemService.deleteAll(cart);

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

            return orderService.findAllByUserWithoutPagination(user).stream().map(OrderMapper::orderToReadableOrder).collect(Collectors.toList());
        }
    }

}
