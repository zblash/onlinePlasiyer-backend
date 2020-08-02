package com.marketing.web.services.cart;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.Customer;
import com.marketing.web.models.User;
import com.marketing.web.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Cart findByCustomer(Customer customer) {
        return cartRepository.findByCustomer(customer).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart",""));
    }

    @Override
    public Cart findById(String id) {
        return cartRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart", id.toString()));
    }

    @Override
    public Cart create(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cartRepository.save(cart);
    }

    @Override
    public Cart update(String id, Cart updatedCart) {
        Cart cart = findById(id);
        cart.setCustomer(updatedCart.getCustomer());
        cart.setCartStatus(updatedCart.getCartStatus());
        return cartRepository.save(cart);
    }

    @Override
    public void delete(Cart cart) {
        cartRepository.delete(cart);
    }
}
