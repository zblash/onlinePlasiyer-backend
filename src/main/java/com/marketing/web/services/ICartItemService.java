package com.marketing.web.services;


import com.marketing.web.models.CartItem;

import java.util.List;

public interface ICartItemService {

    List<CartItem> findAll();

    CartItem findById(Long id);

    CartItem create(CartItem cartItem);

    CartItem update(CartItem cartItem,CartItem updatedCartItem);

    void delete(CartItem cartItem);

}
