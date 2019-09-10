package com.marketing.web.services.order;

import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;

import java.util.List;

public interface IOrderService {

    List<Order> findAll();

    List<Order> findAllByFilter(SearchOrder searchOrder);

    List<Order> findAllByFilterAndUser(SearchOrder searchOrder, User user);

    List<Order> findAllByUser(User user);

    Order findById(Long id);

    Order findByUUID(String uuid);

    List<Order> createAll(List<Order> orders);

    List<Order> findByBuyer(Long id);

    Order findBySellerAndUUid(Long selerId, String uuid);

    Order update(Long id, Order updatedOrder);
}
