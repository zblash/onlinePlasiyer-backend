package com.marketing.web.services.cart;


import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.CartItemHolder;
import com.marketing.web.models.ProductSpecify;

import java.util.List;

public interface CartItemService {

    List<CartItem> findAll();

    CartItem findById(String id);

    CartItem create(CartItem cartItem);

    CartItem update(String id, CartItem updatedCartItem);

    void delete(Cart cart, CartItem cartItem);

    void deleteAllByCart(Cart cart);

    CartItem createOrUpdate(CartItemHolder cartItemHolder, int quantity, ProductSpecify productSpecify);

}
