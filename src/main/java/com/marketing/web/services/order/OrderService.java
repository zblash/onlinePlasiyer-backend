package com.marketing.web.services.order;

import com.marketing.web.dtos.order.OrderSummary;
import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Page<Order> findAll(int pageNumber, String sortBy, String sortType);

    OrderSummary groupBy(User user);

    Page<Order> findAllByFilter(SearchOrder searchOrder, int pageNumber);

    Page<Order> findAllByFilterAndUser(SearchOrder searchOrder, User user, int pageNumber);

    Page<Order> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    List<Order> findAllByUserWithoutPagination(User user);

    Order findById(Long id);

    Order findByUUID(String uuid);

    List<Order> createAll(List<Order> orders);

    Order findByUuidAndUser(String uuid, User user, RoleType roleType);

    Order update(String uuid, Order updatedOrder);

}
