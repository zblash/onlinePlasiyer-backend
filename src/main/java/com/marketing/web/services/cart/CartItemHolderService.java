package com.marketing.web.services.cart;

import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItemHolder;

import java.util.List;
import java.util.Optional;

public interface CartItemHolderService {

    List<CartItemHolder> findAll();

    CartItemHolder findById(Long id);

    CartItemHolder findByUUID(String uuid);

    CartItemHolder findByCartAndUuid(Cart cart, String holderId);

    Optional<CartItemHolder> findByCartAndSeller(Long cartId, String userId);

    CartItemHolder create(CartItemHolder cartItemHolder);

    CartItemHolder update(Long id, CartItemHolder updatedCartItemHolder);

    void delete(Cart cart, CartItemHolder cartItemHolder);

    void deleteAll(List<CartItemHolder> cartItemHolders);
}
