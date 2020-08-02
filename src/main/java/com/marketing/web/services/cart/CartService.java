package com.marketing.web.services.cart;


import com.marketing.web.models.Cart;
import com.marketing.web.models.Customer;
import com.marketing.web.models.User;

import java.util.List;

public interface CartService {

    List<Cart> findAll();

    Cart findByCustomer(Customer customer);

    Cart findById(String id);

    Cart create(Customer customer);

    Cart update(String id,Cart updatedCart);

    void delete(Cart cart);
}
