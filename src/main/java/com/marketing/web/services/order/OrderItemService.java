package com.marketing.web.services.order;

import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {

    List<OrderItem> saveAll(List<OrderItem> orderItems);

    OrderItem findById(String id);

    List<OrderItem> findByOrder(Order order);

    void deleteAllByUuid(List<OrderItem> removedItems);

    OrderItem findByIdAndOrder(String id, Order order);
}
