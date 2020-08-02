package com.marketing.web.repositories;

import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem,UUID> {

    Optional<CartItem> findByCart_IdAndProduct_Id(UUID cartId, UUID id);

    List<CartItem> deleteAllByCart(Cart cart);
}
