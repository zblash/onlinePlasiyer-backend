package com.marketing.web.services.order;

import com.marketing.web.dtos.order.OrderSummary;
import com.marketing.web.dtos.order.SearchOrder;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderService {

    Page<Order> findAll(int pageNumber, String sortBy, String sortType);

    List<Order> findAll();

    OrderSummary groupBy(Merchant merchant);

    Page<Order> findAllBySpecification(Specification<Order> specification, Integer pageNumber, String sortBy, String sortType);

    List<Order> findAllBySpecification(Specification<Order> specification);

    Order findById(String id);

    List<Order> createAll(List<Order> orders);

    Order findByIdAndMerchant(String id, Merchant merchant);

    Order findByIdAndCustomer(String id, Customer customer);

    Order update(String id, Order updatedOrder);

    byte[] orderToPDF(Order order);

    byte[] orderToExcel(Order order);

}
