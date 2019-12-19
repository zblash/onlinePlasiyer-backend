package com.marketing.web.services.cart;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Cart findByUser(User user) {
        return cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    @Override
    public Cart findById(Long id) {
        return cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: "+ id));
    }

    @Override
    public Cart findByUUID(String uuid) {
        return cartRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: "+ uuid));
    }

    @Override
    public Cart create(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public Cart update(Long id, Cart updatedCart) {
        Cart cart = findById(id);
        cart.setPaymentOption(updatedCart.getPaymentOption());
        cart.setUser(updatedCart.getUser());
        cart.setCartStatus(updatedCart.getCartStatus());
        return cartRepository.save(cart);
    }

    @Override
    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }
}
