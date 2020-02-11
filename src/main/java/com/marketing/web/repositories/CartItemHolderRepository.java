package com.marketing.web.repositories;

import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItemHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemHolderRepository extends JpaRepository<CartItemHolder, Long> {

    Optional<CartItemHolder> findBySellerId(String userId);

    Optional<CartItemHolder> findByUuid(UUID uuid);

    Optional<CartItemHolder> findByCartAndSellerId(Cart cart, String sellerId);

    Optional<CartItemHolder> findByCartAndUuid(Cart cart, UUID uuid);
}
