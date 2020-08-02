package com.marketing.web.services.cart;

import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItemHolder;
import com.marketing.web.models.Merchant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CartItemHolderService {

    List<CartItemHolder> findAll();

    CartItemHolder findById(String id);

    CartItemHolder findByCartAndUuid(Cart cart, String holderId);

    Optional<CartItemHolder> findByCartAndMerchant(Cart cart, String merchantId);

    CartItemHolder create(CartItemHolder cartItemHolder);

    CartItemHolder update(String id, CartItemHolder updatedCartItemHolder);

    void delete(Cart cart, CartItemHolder cartItemHolder);

    void deleteAll(Collection<CartItemHolder> cartItemHolders);
}
