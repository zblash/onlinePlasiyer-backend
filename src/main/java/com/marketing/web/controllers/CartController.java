package com.marketing.web.controllers;

import com.marketing.web.dtos.CartDTO;
import com.marketing.web.dtos.CartItemDTO;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.CustomPrincipal;
import com.marketing.web.models.Order;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.services.impl.CartItemService;
import com.marketing.web.services.impl.OrderItemService;
import com.marketing.web.services.impl.OrderService;
import com.marketing.web.services.impl.ProductService;
import com.marketing.web.services.impl.ProductSpecifyService;
import com.marketing.web.utils.mappers.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    private Logger logger = LoggerFactory.getLogger(CartController.class);

    @GetMapping
    public ResponseEntity<CartDTO> getCart(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();

        CartDTO cartDTO = CartMapper.INSTANCE.cartToCartDTO(user.getCart());
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/addItem")
    public ResponseEntity<?> addItem(@Valid @RequestBody CartItemDTO cartItemDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        List<State> productStates = productSpecifyService.findById(cartItemDTO.getProductId()).getStates();
        if (user.getActiveStates().containsAll(productStates)) {
            CartItem cartItem = cartItemService.createOrUpdate(user.getCart(), cartItemDTO);
            return ResponseEntity.ok(cartItem);
        }
        return new ResponseEntity<>("You can't order this product", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/removeItem/{id}")
    public ResponseEntity<String> removeItem(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        cartItemService.delete(user.getCart(),cartItemService.findById(id));
        return ResponseEntity.ok("Removed Item from User's cart with id:"+id);
    }

    @PostMapping("/updateItem/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id,@Valid @RequestBody CartItemDTO cartItemDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        CartItem foundItem = cartItemService.findById(id);
        if (cartItemDTO.getQuantity() < 1){
            cartItemService.delete(user.getCart(),foundItem);
            return ResponseEntity.ok("Removed Item from User's cart with id:"+id);
        }

        CartItem cartItem = cartItemService.update(user.getCart(), foundItem, cartItemService.cartItemDTOtoCartItem(cartItemDTO));
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/clear")
    public ResponseEntity<?> clearCart(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        cartItemService.deleteAll(user.getCart());
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        Cart cart = user.getCart();

        if (!cart.getItems().isEmpty() && cart.getItems() != null) {
            List<Order> orders = orderService.createAll(user, cart.getItems());
            orderItemService.createAll(cart.getItems(), orders);
            cartItemService.deleteAll(cart);
            return ResponseEntity.ok(orderService.findByBuyer(user.getId()));
        }
        return ResponseEntity.ok("Cart is empty");
    }
}
