package com.marketing.web.repositories;

import com.marketing.web.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByBuyer_Id(Long id);

    List<Order> findAllBySeller_Id(Long id);

    Optional<Order> findByBuyer_IdAndId(Long buyerId, Long id);

    Optional<Order> findBySeller_IdAndId(Long sellerId, Long id);
}
