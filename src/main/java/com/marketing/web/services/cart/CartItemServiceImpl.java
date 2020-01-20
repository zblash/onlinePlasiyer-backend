package com.marketing.web.services.cart;

import com.marketing.web.dtos.cart.WritableCartItem;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.Promotion;
import com.marketing.web.repositories.CartItemRepository;
import com.marketing.web.services.product.ProductSpecifyServiceImpl;
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
    private ProductSpecifyServiceImpl productSpecifyService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + id));
    }

    @Override
    public CartItem findByUUID(String uuid) {
        return cartItemRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + uuid));
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

        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(c -> c.getUuid().toString().equals(cartItem.getUuid().toString()))
                .findFirst();
        if (optionalCartItem.isPresent()) {
            cartItemRepository.delete(optionalCartItem.get());
        } else {
            throw new ResourceNotFoundException("CartItem not found");
        }
    }

    @Override
    public void deleteAll(List<CartItem> cartItems) {
        cartItemRepository.deleteAll(cartItems);
    }

    @Override
    public CartItem createOrUpdate(Cart cart, WritableCartItem writableCartItem) {
        CartItem cartItem = cartItemDTOtoCartItem(writableCartItem);

        if (!cart.getItems().isEmpty() && cart.getItems() != null) {
            Optional<CartItem> optionalCartItem = cart.getItems().stream()
                    .filter(c -> c.getProduct().getId().equals(cartItem.getProduct().getId()))
                    .findFirst();
            if (optionalCartItem.isPresent()) {
                CartItem foundItem = optionalCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + foundItem.getQuantity());
                cartItem.setTotalPrice(cartItem.getTotalPrice() + foundItem.getTotalPrice());
                if (foundItem.getProduct().getPromotion() != null){
                    cartItem.setPromotion(cartItem.getProduct().getPromotion());
                    cartItem.setDiscountedTotalPrice(discountCalculator(cartItem, foundItem.getProduct()));
                }
                return update(foundItem.getUuid().toString(), cartItem);
            }
        }

        cartItem.setCart(cart);
        return create(cartItem);
    }

    private CartItem cartItemDTOtoCartItem(WritableCartItem writableCartItem) {
        ProductSpecify product = productSpecifyService.findByUUID(writableCartItem.getProductId());
        if (product.getQuantity() < writableCartItem.getQuantity()) {
            throw new BadRequestException("Cart item quantity must smaller or equal product quantity");
        }
        CartItem cartItem = new CartItem();
        double totalPrice = product.getTotalPrice() * cartItem.getQuantity();
        cartItem.setProduct(product);
        cartItem.setQuantity(writableCartItem.getQuantity());
        cartItem.setTotalPrice(totalPrice);
        if (product.getPromotion() != null) {
            cartItem.setDiscountedTotalPrice(discountCalculator(cartItem, product));
            cartItem.setPromotion(product.getPromotion());
        }
        else {
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
