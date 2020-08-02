package com.marketing.web.services.order;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import com.marketing.web.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
       return orderItemRepository.saveAll(orderItems);
    }

    @Override
    public OrderItem findById(String id) {
        return orderItemRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order.item",id));
    }

    @Override
    public List<OrderItem> findByOrder(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    @Override
    public void deleteAllByUuid(List<OrderItem> removedItems) {
        orderItemRepository.deleteAll(removedItems);
    }

    @Override
    public OrderItem findByIdAndOrder(String id, Order order) {
        return orderItemRepository.findByIdAndOrder(UUID.fromString(id), order).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order.item",id));
    }
}
