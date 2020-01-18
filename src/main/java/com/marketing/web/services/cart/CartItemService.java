package com.marketing.web.services.cart;


import com.marketing.web.dtos.cart.WritableCartItem;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;

import java.util.List;

public interface CartItemService {

    List<CartItem> findAll();

    CartItem findById(Long id);

    CartItem findByUUID(String uuid);

    CartItem create(CartItem cartItem);

    CartItem update(String id, CartItem updatedCartItem);

    void delete(Cart cart, CartItem cartItem);

    void deleteAll(List<CartItem> cartItems);

    CartItem createOrUpdate(Cart cart, WritableCartItem writableCartItem);

}
