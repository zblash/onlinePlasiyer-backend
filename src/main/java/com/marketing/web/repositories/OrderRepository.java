package com.marketing.web.repositories;

import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findAllByOrderByIdDesc(Pageable pageable);

    List<Order> findAllByBuyer_Id(Long id);

    List<Order> findAllBySeller_Id(Long id);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    List<Order> findAllByOrderDateRange(Date startDate, Date endDate);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2 and o.seller = ?3 or o.buyer = ?4")
    List<Order> findAllByOrderDateRangeAndUsers(Date startDate, Date endDate, Long sellerId, Long buyerId);

    @Query("SELECT o FROM Order o WHERE o.uuid = ?1 and o.seller = ?2 or o.buyer = ?3")
    List<Order> findAllByUuidAndUsers(Long sellerId, Long buyerId);

    Optional<Order> findBySeller_IdAndUuid(Long sellerId, UUID uuid);

    Optional<Order> findByUuid(UUID uuid);

    Page<Order> findAllBySellerOrBuyerOrderByIdDesc(User seller, User buyer, Pageable pageable);

    List<Order> findAllBySellerOrBuyerOrderByIdDesc(User seller, User buyer);

    Optional<Order> findByUuidAndAndBuyerOrSeller(UUID uuid,User buyer,User seller);
}
