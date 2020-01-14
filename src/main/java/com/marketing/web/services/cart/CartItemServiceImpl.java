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

    private Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);

    @Override
    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: "+ id));
    }

    @Override
    public CartItem findByUUID(String uuid) {
        return cartItemRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: "+ uuid));
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
            cartItem.setQuantity(updatedCartItem.getQuantity());
            cartItem.setTotalPrice(updatedCartItem.getTotalPrice());
            return cartItemRepository.save(cartItem);
        } else {
            throw new ResourceNotFoundException("CartItem not found");
        }
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
    public void deleteAll(Cart cart) {
        for (CartItem cartItem : cart.getItems()){
            logger.info(Long.toString(cartItem.getId()));
            cartItemRepository.delete(cartItemRepository.findById(cartItem.getId()).orElse(null));
        }
    }

    @Override
    public CartItem createOrUpdate(Cart cart, WritableCartItem writableCartItem){
        CartItem cartItem = cartItemDTOtoCartItem(writableCartItem);

        if (!cart.getItems().isEmpty() && cart.getItems() != null) {
            Optional<CartItem> optionalCartItem = cart.getItems().stream()
                    .filter(c -> c.getProduct().getId().equals(cartItem.getProduct().getId()))
                    .findFirst();
            if (optionalCartItem.isPresent()) {
                CartItem foundItem = optionalCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + foundItem.getQuantity());
                cartItem.setTotalPrice(cartItem.getTotalPrice() + foundItem.getTotalPrice());
                return update(cart, foundItem, cartItem);
            }
        }

        cartItem.setCart(cart);
        return create(cartItem);
    }

    private CartItem cartItemDTOtoCartItem(WritableCartItem writableCartItem){
        ProductSpecify product = productSpecifyService.findByUUID(writableCartItem.getProductId());
        if (product.getQuantity() < writableCartItem.getQuantity()){
            throw new BadRequestException("Cart item quantity must smaller or equal product quantity");
        }
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        if (product.getPromotion() != null) {
            Promotion promotion = product.getPromotion();
            double totalPrice = product.getTotalPrice();
            if (writableCartItem.getQuantity() >= promotion.getDiscountUnit()){
                double notDiscountedPrice = totalPrice * promotion.getDiscountUnit();
                totalPrice = product.getTotalPrice() - ((notDiscountedPrice * promotion.getDiscountValue()) / 100);
            }
            cartItem.setDiscountedTotalPrice(totalPrice);
            cartItem.setPromotion(product.getPromotion());

        }
        cartItem.setQuantity(writableCartItem.getQuantity());
        cartItem.setTotalPrice(product.getTotalPrice() * cartItem.getQuantity());
        return cartItem;
    }
}
