package com.marketing.web.services.order;

import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import java.util.List;

public interface IOrderItemService {

    List<OrderItem> createAll(List<OrderItem> orderItems);

    OrderItem findByUUID(String uuid);

    List<OrderItem> findByOrder(Order order);

}
