package com.marketing.web.services.cart;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItemHolder;
import com.marketing.web.repositories.CartItemHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartItemHolderServiceImpl implements CartItemHolderService {

    @Autowired
    private CartItemHolderRepository cartItemHolderRepository;

    @Override
    public List<CartItemHolder> findAll() {
        return cartItemHolderRepository.findAll();
    }

    @Override
    public CartItemHolder findById(Long id) {
        return cartItemHolderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CartItem Holder not found with id: " + id));
    }

    @Override
    public CartItemHolder findByUUID(String uuid) {
        return cartItemHolderRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("CartItem Holder not found with id: " + uuid));
    }

    @Override
    public CartItemHolder findByCartAndUuid(Cart cart, String holderId) {
        return cartItemHolderRepository.findByCartAndUuid(cart, UUID.fromString(holderId)).orElseThrow(() -> new ResourceNotFoundException("CartItem Holder not found with id: " + holderId));
    }

    @Override
    public Optional<CartItemHolder> findByCartAndSeller(Long cartId, String userId) {
       return cartItemHolderRepository.findByCart_IdAndSellerId(cartId, userId);
    }

    @Override
    public CartItemHolder create(CartItemHolder cartItemHolder) {
        return cartItemHolderRepository.save(cartItemHolder);
    }

    @Override
    public CartItemHolder update(Long id, CartItemHolder updatedCartItemHolder) {
        CartItemHolder cartItemHolder = findById(id);
        cartItemHolder.setCart(updatedCartItemHolder.getCart());
        cartItemHolder.setCartItems(updatedCartItemHolder.getCartItems());
        cartItemHolder.setPaymentOption(updatedCartItemHolder.getPaymentOption());
        cartItemHolder.setSellerId(updatedCartItemHolder.getSellerId());
        cartItemHolder.setSellerName(updatedCartItemHolder.getSellerName());
        return cartItemHolderRepository.save(cartItemHolder);
    }

    @Override
    public void delete(Cart cart, CartItemHolder cartItemHolder) {
        Optional<CartItemHolder> optionalCartItemHolder = cartItemHolderRepository.findByCart_IdAndSellerId(cart.getId(),cartItemHolder.getSellerId());
        if (optionalCartItemHolder.isPresent()) {
            cartItemHolderRepository.delete(optionalCartItemHolder.get());
        } else {
            throw new ResourceNotFoundException("CartItem Holder not found");
        }
    }

    @Override
    public void deleteAll(List<CartItemHolder> cartItemHolders) {
        cartItemHolderRepository.deleteAll(cartItemHolders);
    }
}
