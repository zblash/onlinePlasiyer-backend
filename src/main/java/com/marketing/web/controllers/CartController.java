package com.marketing.web.controllers;

import com.marketing.web.dtos.cart.ReadableCart;
import com.marketing.web.dtos.cart.WritableCartItem;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.cart.CartService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.product.ProductSpecifyServiceImpl;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

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

    @PostMapping("/addItem")
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

    @PostMapping("/removeItem/{id}")
    public ResponseEntity<ReadableCart> removeItem(@PathVariable String id){
        User user = userService.getLoggedInUser();

        cartItemService.delete(user.getCart(),cartItemService.findByUUID(id));
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
    }


    @PostMapping("/clear")
    public ResponseEntity<ReadableCart> clearCart(){
        User user = userService.getLoggedInUser();

        cartItemService.deleteAll(user.getCart());
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<List<ReadableOrder>> checkout(){
        User user = userService.getLoggedInUser();
        Cart cart = user.getCart();

        if (!cart.getItems().isEmpty() && cart.getItems() != null) {
            return ResponseEntity.ok(orderFacade.checkoutCart(user,cart,cart.getItems()));
        }
        throw new ResourceNotFoundException("There are no items in your cart");
    }
}
