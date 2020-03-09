package com.marketing.web.services.order;

import com.marketing.web.dtos.order.OrderSummary;
import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface OrderService {

    Page<Order> findAll(int pageNumber, String sortBy, String sortType);

    OrderSummary groupBy(User user);

    Page<Order> findAllByFilter(Date startDate, Date endDate, Integer pageNumber, String sortBy, String sortType);

    Page<Order> findAllByFilterAndUser(Date startDate, Date endDate, User user, Integer pageNumber, String sortBy, String sortType);

    Page<Order> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    List<Order> findAllByUserWithoutPagination(User user);

    Order findById(Long id);

    Order findByUUID(String uuid);

    List<Order> createAll(List<Order> orders);

    Order findByUuidAndUser(String uuid, User user, RoleType roleType);

    Order update(String uuid, Order updatedOrder);

    Page<Order> findAllByUsers(User user1, User user2, Integer pageNumber, String sortBy, String sortType);

    Page<Order> findAllByFilterAndUsers(Date startDate, Date endDate, User user1, User user2,Integer pageNumber, String sortBy, String sortType);
}
