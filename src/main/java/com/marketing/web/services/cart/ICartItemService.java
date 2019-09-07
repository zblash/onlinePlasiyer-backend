package com.marketing.web.services.cart;


import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;

import java.util.List;

public interface ICartItemService {

    List<CartItem> findAll();

    CartItem findById(Long id);

    CartItem findByUUID(String uuid);

    CartItem create(CartItem cartItem);

    CartItem update(Cart cart, CartItem cartItem,CartItem updatedCartItem);

    void delete(Cart cart, CartItem cartItem);

    void deleteAll(Cart cart);

}
