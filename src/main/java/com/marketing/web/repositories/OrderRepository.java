package com.marketing.web.repositories;

import com.marketing.web.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByBuyer_Id(Long id);

    List<Order> findAllBySeller_Id(Long id);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    List<Order> findAllByOrderDateRange(Date startDate, Date endDate);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2 and o.seller = ?3 and o.buyer = ?4")
    List<Order> findAllByOrderDateRangeAndUsers(Date startDate, Date endDate, Long sellerId, Long buyerId);

    @Query("SELECT o FROM Order o WHERE o.seller = ?1 and o.buyer = ?2")
    List<Order> findAllByUsers(Long sellerId, Long buyerId);

    Optional<Order> findByBuyer_IdAndId(Long buyerId, Long id);

    Optional<Order> findBySeller_IdAndId(Long sellerId, Long id);

    Optional<Order> findByBuyer_IdAndUuid(Long buyerId, UUID uuid);

    Optional<Order> findBySeller_IdAndUuid(Long sellerId, UUID uuid);

    Optional<Order> findByUuid(UUID uuid);
}
