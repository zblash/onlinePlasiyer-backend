package com.marketing.web.services.order;

import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;

import java.util.List;

public interface IOrderService {

    Order findById(Long id);

    List<Order> createAll(List<Order> orders);

    List<Order> findByBuyer(Long id);

    Order findByBuyerAndId(Long buyerId, Long id);

    Order findBySellerAndId(Long selerId, Long id);

    List<Order> findBySeller(Long id);

    Order update(Long id, Order updatedOrder);
}
