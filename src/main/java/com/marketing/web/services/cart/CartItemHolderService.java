package com.marketing.web.services.cart;

import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItemHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CartItemHolderService {

    List<CartItemHolder> findAll();

    CartItemHolder findById(Long id);

    CartItemHolder findByUUID(String uuid);

    CartItemHolder findByCartAndUuid(Cart cart, String holderId);

    Optional<CartItemHolder> findByCartAndSeller(Cart cart, String userId);

    CartItemHolder create(CartItemHolder cartItemHolder);

    CartItemHolder update(Long id, CartItemHolder updatedCartItemHolder);

    void delete(Cart cart, CartItemHolder cartItemHolder);

    void deleteAll(Set<CartItemHolder> cartItemHolders);
}
