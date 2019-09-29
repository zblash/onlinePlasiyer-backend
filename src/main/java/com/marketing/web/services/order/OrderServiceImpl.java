package com.marketing.web.services.order;

import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import com.marketing.web.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> findAll(){
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findAllByFilter(SearchOrder searchOrder) {
        return orderRepository.findAllByOrderDateRange(searchOrder.getStartDate(),searchOrder.getEndDate());
    }

    @Override
    public List<Order> findAllByFilterAndUser(SearchOrder searchOrder, User user) {
        return orderRepository.findAllByOrderDateRangeAndUsers(searchOrder.getStartDate(),searchOrder.getEndDate(),user.getId(),user.getId());
    }

    @Override
    public List<Order> findAllByUser(User user){
        return orderRepository.findAllBySellerOrBuyer(user,user);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + id));
    }

    @Override
    public Order findByUUID(String uuid) {
        return orderRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Order not found with id:" + uuid));
    }

    @Override
    public List<Order> createAll(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }


    @Override
    public Order findByUuidAndUser(String uuid,User user) {
        return orderRepository.findByUuidAndAndBuyerOrSeller(UUID.fromString(uuid),user,user).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: "+ uuid));
    }

    @Override
    public Order update(String uuid, Order updatedOrder) {
        Order order = findByUUID(uuid);
        order.setWaybillDate(updatedOrder.getWaybillDate());
        order.setStatus(updatedOrder.getStatus());
        order.setTotalPrice(updatedOrder.getTotalPrice());
        return orderRepository.save(order);
    }

}
