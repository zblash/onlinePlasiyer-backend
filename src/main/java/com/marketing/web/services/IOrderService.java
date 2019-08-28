package com.marketing.web.services;

import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;

import java.util.List;

public interface IOrderService {

    List<Order> createAll(User user, List<CartItem> cartItems);

    List<Order> findByBuyer(Long id);

    Order findByBuyerAndId(Long buyerId, Long id);

    List<Order> findBySeller(Long id);

}
