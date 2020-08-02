package com.marketing.web.repositories;

import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItemHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemHolderRepository extends JpaRepository<CartItemHolder, UUID> {

    Optional<CartItemHolder> findByMerchantId(String merchantId);


    Optional<CartItemHolder> findByCartAndMerchantId(Cart cart, String merchantId);

    Optional<CartItemHolder> findByCartAndId(Cart cart, UUID uuid);
}
