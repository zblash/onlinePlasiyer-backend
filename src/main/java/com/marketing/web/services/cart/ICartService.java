package com.marketing.web.services.cart;


import com.marketing.web.models.Cart;
import com.marketing.web.models.User;

import java.util.List;

public interface ICartService {

    List<Cart> findAll();

    Cart findById(Long id);

    Cart findByUUID(String uuid);

    Cart create(User user);

    Cart update(Cart cart,Cart updatedCart);

    void delete(Cart cart);
}
