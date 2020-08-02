package com.marketing.web.repositories;

import com.marketing.web.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o.status AS status, COUNT(o.status) AS cnt FROM Order o WHERE o.merchant = ?1 GROUP BY o.status")
    List<OrderGroup> groupBy(Merchant merchant);

    Page<Order> findAllByOrOrderDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Order> findAllByOrderDateBetweenAndCustomerOrMerchant(LocalDate startDate, LocalDate endDate, Customer customer, Merchant merchant, Pageable pageable);

    Page<Order> findAllByMerchantOrCustomer(Merchant merchant, Customer customer, Pageable pageable);

    Page<Order> findAllByMerchantAndCustomer(Merchant merchant, Customer customer, Pageable pageable);

    Page<Order> findAllByOrderDateBetweenAndMerchantAndCustomer(LocalDate startDate, LocalDate endDate, Merchant merchant, Customer customer, Pageable pageable);

    List<Order> findAllByMerchantOrCustomerOrderByIdDesc(Merchant merchant, Customer customer);

    Optional<Order> findByMerchantAndId(Merchant merchant, UUID id);

    Optional<Order> findByCustomerAndId(Customer customer, UUID id);

}
