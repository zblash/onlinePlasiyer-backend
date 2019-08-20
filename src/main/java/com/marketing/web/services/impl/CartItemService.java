package com.marketing.web.services.impl;

import com.marketing.web.dtos.CartItemDTO;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.repositories.CartItemRepository;
import com.marketing.web.services.ICartItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService implements ICartItemService {

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Logger logger = LoggerFactory.getLogger(CartItemService.class);

    @Override
    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public CartItem create(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem update(Cart cart, CartItem cartItem, CartItem updatedCartItem) {
        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(c -> c.getId().equals(cartItem.getId()))
                .findFirst();
        if (optionalCartItem.isPresent()) {
            cartItem.setProduct(updatedCartItem.getProduct());
            cartItem.setCart(updatedCartItem.getCart());
            cartItem.setQuantity(updatedCartItem.getQuantity());
            cartItem.setTotalPrice(updatedCartItem.getTotalPrice());
            return cartItemRepository.save(cartItem);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void delete(Cart cart, CartItem cartItem) {

        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(c -> c.getProduct().getId().equals(cartItem.getId()))
                .findFirst();
        if (optionalCartItem.isPresent()) {
            cartItemRepository.delete(optionalCartItem.get());
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void deleteAll(Cart cart) {
        cartItemRepository.deleteAll(cart.getItems());
    }

    public CartItem createOrUpdate(Cart cart, CartItemDTO cartItemDTO){
        CartItem cartItem = cartItemDTOtoCartItem(cartItemDTO);

        if (!cart.getItems().isEmpty() && cart.getItems() != null) {
            Optional<CartItem> optionalCartItem = cart.getItems().stream()
                    .filter(c -> c.getProduct().getId().equals(cartItem.getProduct().getId()))
                    .findFirst();
            if (optionalCartItem.isPresent()) {
                CartItem foundItem = optionalCartItem.get();
                return update(cart, foundItem, cartItem);
            }
        }

        cartItem.setCart(cart);
        return create(cartItem);
    }

    public CartItem cartItemDTOtoCartItem(CartItemDTO cartItemDTO){
        CartItem cartItem = new CartItem();
        ProductSpecify product = productSpecifyService.findById(cartItemDTO.getProductId());
        cartItem.setProduct(product);
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setTotalPrice(product.getTotalPrice() * cartItem.getQuantity());
        return cartItem;
    }
}
