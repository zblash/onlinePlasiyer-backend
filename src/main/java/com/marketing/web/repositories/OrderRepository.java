package com.marketing.web.repositories;

import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findAll(Pageable pageable);

    List<Order> findAllByBuyer_Id(Long id);

    List<Order> findAllBySeller_Id(Long id);

    @Query("SELECT o.status AS status, COUNT(o.status) AS cnt FROM Order o WHERE o.buyer = ?1 or o.seller = ?1 GROUP BY o.status")
    List<OrderGroup> groupBy(User user);

    Page<Order> findAllByOrOrderDateBetween(Date startDate, Date endDate, Pageable pageable);

    Page<Order> findAllByOrderDateBetweenAndBuyerOrSeller(Date startDate, Date endDate,User buyer, User seller, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.uuid = ?1 and o.seller = ?2 or o.buyer = ?3")
    List<Order> findAllByUuidAndSeller(Long sellerId, Long buyerId);

    Optional<Order> findBySeller_IdAndUuid(Long sellerId, UUID uuid);

    Optional<Order> findByUuid(UUID uuid);

    Page<Order> findAllBySellerOrBuyer(User seller, User buyer, Pageable pageable);

    List<Order> findAllBySellerOrBuyerOrderByIdDesc(User seller, User buyer);

    Optional<Order> findByUuidAndAndBuyerOrSeller(UUID uuid,User buyer,User seller);
}
