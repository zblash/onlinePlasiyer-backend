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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);

    public CartItemServiceImpl(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem findById(String id) {
        return cartItemRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"cart.item", id.toString()));
    }

    @Override
    public CartItem create(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem update(String id, CartItem updatedCartItem) {
        CartItem cartItem = findById(id);
        cartItem.setProduct(updatedCartItem.getProduct());
        cartItem.setQuantity(updatedCartItem.getQuantity());
        cartItem.setTotalPrice(updatedCartItem.getTotalPrice());
        cartItem.setDiscountedTotalPrice(updatedCartItem.getDiscountedTotalPrice());
        cartItem.setPromotion(cartItem.getPromotion());
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void delete(Cart cart, CartItem cartItem) {

        Optional<CartItem> optionalCartItem = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), cartItem.getProduct().getId());
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
        Optional<CartItem> optionalCartItem = cartItemRepository.findByCart_IdAndProduct_Id(cartItemHolder.getCart().getId(), productSpecify.getId());
        if (optionalCartItem.isPresent()) {
            return update(optionalCartItem.get().getId().toString(), cartItem);
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
        BigDecimal totalPrice = product.getTotalPrice().multiply(BigDecimal.valueOf(quantity));
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

    private BigDecimal discountCalculator(CartItem cartItem, ProductSpecify product) {
        Promotion promotion = product.getPromotion();
        BigDecimal totalPrice = cartItem.getTotalPrice();
        if (cartItem.getQuantity() >= promotion.getDiscountUnit()) {
            BigDecimal notDiscountedPrice = product.getTotalPrice().multiply(BigDecimal.valueOf(promotion.getDiscountUnit()));
            BigDecimal calculatedPrice = notDiscountedPrice.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100), RoundingMode.CEILING);
            totalPrice = cartItem.getTotalPrice().subtract(calculatedPrice);
        }
        return totalPrice;
    }
}
