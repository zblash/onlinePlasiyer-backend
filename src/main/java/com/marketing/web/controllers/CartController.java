package com.marketing.web.controllers;

import com.marketing.web.dtos.cart.PaymentMethod;
import com.marketing.web.dtos.cart.ReadableCart;
import com.marketing.web.dtos.cart.WritableCartItem;
import com.marketing.web.dtos.cart.WritableCheckout;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.enums.CartStatus;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.cart.CartService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private OrderFacade orderFacade;

    private Logger logger = LoggerFactory.getLogger(CartController.class);

    @GetMapping
    public ResponseEntity<ReadableCart> getCart(){
        User user = userService.getLoggedInUser();
        ReadableCart readableCart = CartMapper.cartToReadableCart(user.getCart());
        return ResponseEntity.ok(readableCart);
    }

    @PostMapping
    public ResponseEntity<ReadableCart> addItem(@Valid @RequestBody WritableCartItem writableCartItem){
        User user = userService.getLoggedInUser();

        if (writableCartItem.getQuantity() > 0) {
            List<State> productStates = productSpecifyService.findByUUID(writableCartItem.getProductId()).getStates();
            if (productStates.contains(user.getAddress().getState())) {
                CartItem cartItem = cartItemService.createOrUpdate(user.getCart(), writableCartItem);
                return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
            }
            throw new BadRequestException("You can't order this product");
        }
        throw new BadRequestException("Quantity must bigger than 0");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableCart> removeItem(@PathVariable String id){
        User user = userService.getLoggedInUser();

        cartItemService.delete(user.getCart(),cartItemService.findByUUID(id));
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
    }


    @DeleteMapping
    public ResponseEntity<ReadableCart> clearCart(){
        User user = userService.getLoggedInUser();

        cartItemService.deleteAll(user.getCart().getItems());
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<List<ReadableOrder>> checkout(@Valid @RequestBody WritableCheckout writableCheckout){
        User user = userService.getLoggedInUser();
        Cart cart = user.getCart();

        if (!cart.getItems().isEmpty()
                && cart.getItems() != null
                && Optional.ofNullable(cart.getPaymentOption()).isPresent()
                && CartStatus.PRCD.equals(cart.getCartStatus())) {
            return ResponseEntity.ok(orderFacade.checkoutCart(user, cart, writableCheckout));
        }
        throw new BadRequestException("Can not perform cart");
    }

    @PostMapping("/setPayment")
    public ResponseEntity<ReadableCart> setPayment(@Valid @RequestBody PaymentMethod paymentMethod){
        Cart cart = userService.getLoggedInUser().getCart();

        cart.setCartStatus(CartStatus.PRCD);
        cart.setPaymentOption(paymentMethod.getPaymentOption());
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.update(cart.getId(),cart)));
    }

}
