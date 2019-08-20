package com.marketing.web.controllers;

import com.marketing.web.dtos.CartDTO;
import com.marketing.web.dtos.CartItemDTO;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.CustomPrincipal;
import com.marketing.web.models.User;
import com.marketing.web.services.impl.CartItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartItemService cartItemService;

    private Logger logger = LoggerFactory.getLogger(CartController.class);

    @GetMapping
    public ResponseEntity<Cart> getCart(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();

        List<CartItem> items = user.getCart().getItems();
        double totalPrice = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        int quantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(user.getCart().getItems());
        cartDTO.setTotalPrice(totalPrice);
        cartDTO.setQuantity(quantity);

        return ResponseEntity.ok(user.getCart());
    }

    @PostMapping("/addItem")
    public ResponseEntity<CartItem> addItem(@Valid @RequestBody CartItemDTO cartItemDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        CartItem cartItem = cartItemService.createOrUpdate(user.getCart(),cartItemDTO);
        return ResponseEntity.ok(cartItem);
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
}
