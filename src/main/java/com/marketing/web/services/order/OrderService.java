package com.marketing.web.services.order;

import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Page<Order> findAll(int pageNumber);

    List<Order> findAllByFilter(SearchOrder searchOrder);

    List<Order> findAllByFilterAndUser(SearchOrder searchOrder, User user);

    Page<Order> findAllByUser(User user, int pageNumber);

    Order findById(Long id);

    Order findByUUID(String uuid);

    List<Order> createAll(List<Order> orders);

    Order findByUuidAndUser(String uuid, User user);

    Order update(String uuid, Order updatedOrder);

}
