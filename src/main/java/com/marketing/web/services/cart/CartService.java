package com.marketing.web.services.cart;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CartRepository;
import com.marketing.web.services.cart.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Cart findById(Long id) {
        return cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: "+ id));
    }

    @Override
    public Cart create(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public Cart update(Cart cart, Cart updatedCart) {
        cart.setUser(updatedCart.getUser());
        return cartRepository.save(cart);
    }

    @Override
    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }
}
