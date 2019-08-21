package com.marketing.web.services.impl;

import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import com.marketing.web.models.OrderStatus;
import com.marketing.web.models.User;
import com.marketing.web.repositories.OrderRepository;
import com.marketing.web.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> createAll(User user, List<CartItem> cartItems) {
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
        return orderRepository.saveAll(orders);
    }

    @Override
    public List<Order> findByBuyer(Long id) {
        return orderRepository.findAllByBuyer_Id(id);
    }

    @Override
    public List<Order> findBySeller(Long id) {
        return orderRepository.findAllBySeller_Id(id);
    }
}
