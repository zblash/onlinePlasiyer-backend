package com.marketing.web.services.order;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.dtos.order.OrderSummary;
import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.RoleType;
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
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Order> resultPage = orderRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
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
                case CNRQ:
                    orderSummary.setCancelRequestCount(orderGroup.getCnt().intValue());
                    break;
                case CNCL:
                    orderSummary.setCancelledCount(orderGroup.getCnt().intValue());
                    break;
                case SBMT:
                    orderSummary.setSubmittedCount(orderGroup.getCnt().intValue());
            }
        }
        orderSummary.setId(user.getId() + "ordersummary".hashCode() + user.getUuid().toString());
        return orderSummary;
    }

    @Override
    public Page<Order> findAllByFilter(Date startDate, Date endDate, int pageNumber) {
        PageRequest pageRequest = getPageRequest(pageNumber, "id", "desc");
        Page<Order> resultPage = orderRepository.findAllByOrOrderDateBetween(startDate, endDate, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Order> findAllByFilterAndUser(Date startDate, Date endDate, User user, int pageNumber) {
        PageRequest pageRequest = getPageRequest(pageNumber, "id", "desc");
        Page<Order> resultPage = orderRepository.findAllByOrderDateBetweenAndBuyerOrSeller(startDate,endDate, user, user, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Page<Order> findAllByUser(User user, int pageNumber, String sortBy, String sortType){
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Order> resultPage = orderRepository.findAllBySellerOrBuyer(user,user,pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page",String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public List<Order> findAllByUserWithoutPagination(User user) {
        return orderRepository.findAllBySellerOrBuyerOrderByIdDesc(user,user);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order", id.toString()));
    }

    @Override
    public Order findByUUID(String uuid) {
        return orderRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order", uuid));
    }

    @Override
    public List<Order> createAll(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }


    @Override
    public Order findByUuidAndUser(String uuid,User user, RoleType roleType) {
        if (roleType.equals(RoleType.MERCHANT)){
            return orderRepository.findBySellerAndUuid(user, UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order",uuid));
        }
        return orderRepository.findByBuyerAndUuid(user, UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"order", uuid));
}

    @Override
    public Order update(String uuid, Order updatedOrder) {
        Order order = findByUUID(uuid);
        order.setWaybillDate(updatedOrder.getWaybillDate());
        order.setStatus(updatedOrder.getStatus());
        order.setTotalPrice(updatedOrder.getTotalPrice());
        return orderRepository.save(order);
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
