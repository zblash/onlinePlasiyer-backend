package com.marketing.web.services.order;

import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;

import java.util.List;

public interface IOrderService {

    List<Order> findAllByFilter(SearchOrder searchOrder);

    Order findById(Long id);

    Order findByUUID(String uuid);

    List<Order> createAll(List<Order> orders);

    List<Order> findByBuyer(Long id);

    Order findByBuyerAndId(Long buyerId, Long id);

    Order findBySellerAndId(Long selerId, Long id);

    Order findByBuyerAndUUid(Long buyerId, String uuid);

    Order findBySellerAndUUid(Long selerId, String uuid);

    List<Order> findBySeller(Long id);

    Order update(Long id, Order updatedOrder);
}
