package com.marketing.web.repositories;

import com.marketing.web.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    Optional<OrderItem> findByUuid(UUID uuid);
}
