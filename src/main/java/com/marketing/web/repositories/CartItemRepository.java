package com.marketing.web.repositories;

import com.marketing.web.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    Optional<CartItem> findByUuid(UUID uuid);
}
