package com.marketing.web.services.order;

import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import com.marketing.web.repositories.OrderItemRepository;
import com.marketing.web.services.order.IOrderItemService;
import com.marketing.web.utils.mappers.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService implements IOrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> createAll(List<OrderItem> orderItems) {
       return orderItemRepository.saveAll(orderItems);
    }
}
