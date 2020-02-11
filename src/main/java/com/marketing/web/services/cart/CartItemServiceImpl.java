package com.marketing.web.services.cart;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.*;
import com.marketing.web.repositories.CartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);

    @Override
    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart.item", id.toString()));
    }

    @Override
    public CartItem findByUUID(String uuid) {
        return cartItemRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart.item", uuid));
    }

    @Override
    public CartItem create(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem update(String id, CartItem updatedCartItem) {
        CartItem cartItem = findByUUID(id);
        cartItem.setProduct(updatedCartItem.getProduct());
        cartItem.setQuantity(updatedCartItem.getQuantity());
        cartItem.setTotalPrice(updatedCartItem.getTotalPrice());
        cartItem.setDiscountedTotalPrice(updatedCartItem.getDiscountedTotalPrice());
        cartItem.setPromotion(cartItem.getPromotion());
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void delete(Cart cart, CartItem cartItem) {

        Optional<CartItem> optionalCartItem = cartItemRepository.findByCart_IdAndProduct_Uuid(cart.getId(), cartItem.getProduct().getUuid());
        if (optionalCartItem.isPresent()) {
            cartItemRepository.delete(optionalCartItem.get());
        } else {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart.item","");
        }
    }

    @Override
    public void deleteAllByCart(Cart cart) {
        cartItemRepository.deleteAllByCart(cart);
    }

    @Override
    public CartItem createOrUpdate(CartItemHolder cartItemHolder, int quantity, ProductSpecify productSpecify) {
        CartItem cartItem = cartItemDTOtoCartItem(quantity, productSpecify);

        Optional<CartItem> optionalCartItem = cartItemRepository.findByCart_IdAndProduct_Uuid(cartItemHolder.getCart().getId(), productSpecify.getUuid());
        if (optionalCartItem.isPresent()) {
            logger.info(optionalCartItem.get().getUuid().toString());
            return update(optionalCartItem.get().getUuid().toString(), cartItem);
        }
        cartItem.setCart(cartItemHolder.getCart());
        cartItem.setCartItemHolder(cartItemHolder);
        return create(cartItem);
    }

    private CartItem cartItemDTOtoCartItem(int quantity, ProductSpecify product) {
        if (product.getQuantity() < quantity) {
            throw new BadRequestException("Cart item quantity must smaller or equal product quantity");
        }
        CartItem cartItem = new CartItem();
        double totalPrice = product.getTotalPrice() * quantity;
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(totalPrice);
        if (product.getPromotion() != null) {
            cartItem.setDiscountedTotalPrice(discountCalculator(cartItem, product));
            cartItem.setPromotion(product.getPromotion());
        } else {
            cartItem.setDiscountedTotalPrice(totalPrice);
        }

        return cartItem;
    }

    private double discountCalculator(CartItem cartItem, ProductSpecify product) {
        Promotion promotion = product.getPromotion();
        double totalPrice = 0;
        if (cartItem.getQuantity() >= promotion.getDiscountUnit()) {
            double notDiscountedPrice = product.getTotalPrice() * promotion.getDiscountUnit();
            totalPrice = cartItem.getTotalPrice() - ((notDiscountedPrice * promotion.getDiscountValue()) / 100);
        }
        return totalPrice;
    }
}
