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
    public List<OrderItem> createAll(List<CartItem> cartItems, List<Order> orders) {
       List<OrderItem> orderItems = new ArrayList<>();
       for (CartItem cartItem : cartItems){
           OrderItem orderItem = OrderMapper.INSTANCE.cartItemToOrderItem(cartItem);
           Optional<Order> optionalOrder = orders.stream().filter(order -> order.getSeller().getId().equals(orderItem.getSeller().getId())).findFirst();
           optionalOrder.ifPresent(orderItem::setOrder);
           orderItems.add(orderItem);
       }
       return orderItemRepository.saveAll(orderItems);
    }
}
