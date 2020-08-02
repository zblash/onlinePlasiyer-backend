package com.marketing.web.services.cart;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItemHolder;
import com.marketing.web.repositories.CartItemHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartItemHolderServiceImpl implements CartItemHolderService {

    private final CartItemHolderRepository cartItemHolderRepository;

    public CartItemHolderServiceImpl(CartItemHolderRepository cartItemHolderRepository) {
        this.cartItemHolderRepository = cartItemHolderRepository;
    }

    @Override
    public List<CartItemHolder> findAll() {
        return cartItemHolderRepository.findAll();
    }

    @Override
    public CartItemHolder findById(String id) {
        return cartItemHolderRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart.item.holder", id.toString()));
    }

    @Override
    public CartItemHolder findByCartAndUuid(Cart cart, String holderId) {
        return cartItemHolderRepository.findByCartAndId(cart, UUID.fromString(holderId)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart.item.holder", holderId));
    }

    @Override
    public Optional<CartItemHolder> findByCartAndMerchant(Cart cart, String merchantId) {
       return cartItemHolderRepository.findByCartAndMerchantId(cart, merchantId);
    }

    @Override
    public CartItemHolder create(CartItemHolder cartItemHolder) {
        return cartItemHolderRepository.save(cartItemHolder);
    }

    @Override
    public CartItemHolder update(String id, CartItemHolder updatedCartItemHolder) {
        CartItemHolder cartItemHolder = findById(id);
        cartItemHolder.setCart(updatedCartItemHolder.getCart());
        cartItemHolder.setCartItems(updatedCartItemHolder.getCartItems());
        cartItemHolder.setPaymentOption(updatedCartItemHolder.getPaymentOption());
        cartItemHolder.setMerchantId(updatedCartItemHolder.getMerchantId());
        cartItemHolder.setMerchantName(updatedCartItemHolder.getMerchantName());
        return cartItemHolderRepository.save(cartItemHolder);
    }

    @Override
    public void delete(Cart cart, CartItemHolder cartItemHolder) {
        Optional<CartItemHolder> optionalCartItemHolder = findByCartAndMerchant(cart,cartItemHolder.getMerchantId());
        if (optionalCartItemHolder.isPresent()) {
            cartItemHolderRepository.delete(optionalCartItemHolder.get());
        } else {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart.item.holder", "");
        }
    }

    @Override
    public void deleteAll(Collection<CartItemHolder> cartItemHolders) {
        cartItemHolderRepository.deleteAll(cartItemHolders);
    }
}
