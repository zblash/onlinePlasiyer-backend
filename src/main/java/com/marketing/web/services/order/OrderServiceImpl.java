package com.marketing.web.services.order;

import com.marketing.web.dtos.order.OrderSummary;
import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Order;
import com.marketing.web.repositories.OrderGroup;
import com.marketing.web.models.User;
import com.marketing.web.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Page<Order> findAll(int pageNumber, String sortBy, String sortType){
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
        Page<Order> resultPage = orderRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public OrderSummary groupBy(User user) {
        List<OrderGroup> orderGroups = orderRepository.groupBy(user);
        OrderSummary orderSummary = new OrderSummary();
        for (OrderGroup orderGroup : orderGroups){
            switch (OrderStatus.fromValue(orderGroup.getStatus())){
                case NEW :
                    orderSummary.setNewCount(orderGroup.getCnt().intValue());
                    break;
                case FNS:
                    orderSummary.setFinishedCount(orderGroup.getCnt().intValue());
                    break;
                case PAD:
                    orderSummary.setPaidCount(orderGroup.getCnt().intValue());
                    break;
                case CNCL:
                    orderSummary.setCancelledCount(orderGroup.getCnt().intValue());
                    break;
            }
        }
        orderSummary.setId(user.getId() + "ordersummary".hashCode() + user.getUuid().toString());
        return orderSummary;
    }

    @Override
    public Page<Order> findAllByFilter(SearchOrder searchOrder, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15);
        Page<Order> resultPage = orderRepository.findAllByOrOrderDateBetween(searchOrder.getStartDate(),searchOrder.getEndDate(), pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Order> findAllByFilterAndUser(SearchOrder searchOrder, User user, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15);
        Page<Order> resultPage = orderRepository.findAllByOrderDateBetweenAndBuyerOrSeller(searchOrder.getStartDate(),searchOrder.getEndDate(),user, user, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public Page<Order> findAllByUser(User user, int pageNumber, String sortBy, String sortType){
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
        Page<Order> resultPage = orderRepository.findAllBySellerOrBuyer(user,user,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException("Not Found Page Number:" + pageNumber);
        }
        return resultPage;
    }

    @Override
    public List<Order> findAllByUserWithoutPagination(User user) {
        return orderRepository.findAllBySellerOrBuyerOrderByIdDesc(user,user);
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
