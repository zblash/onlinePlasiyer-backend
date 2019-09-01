package com.marketing.web.controllers;

import com.marketing.web.dtos.cart.CartDTO;
import com.marketing.web.dtos.cart.CartItemDTO;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.security.CustomPrincipal;
import com.marketing.web.models.Order;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.order.OrderItemService;
import com.marketing.web.services.order.OrderService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.utils.facade.OrderFacade;
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
import java.util.List;

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

    @Autowired
    private OrderFacade orderFacade;

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
        if (cartItemDTO.getQuantity() > 1) {
            List<State> productStates = productSpecifyService.findById(cartItemDTO.getProductId()).getStates();
            if (user.getActiveStates().containsAll(productStates)) {
                CartItem cartItem = cartItemService.createOrUpdate(user.getCart(), cartItemDTO);
                return ResponseEntity.ok(cartItem);
            }
            return new ResponseEntity<>("You can't order this product", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Quantity must bigger than 0", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/removeItem/{id}")
    public ResponseEntity<String> removeItem(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomPrincipal) auth.getPrincipal()).getUser();
        cartItemService.delete(user.getCart(),cartItemService.findById(id));
        return ResponseEntity.ok("Removed Item from User's cart with id:"+id);
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
            List<ReadableOrder> readableOrders = orderFacade.checkoutCart(user,cart,cart.getItems());
            return ResponseEntity.ok(readableOrders);
        }
        return new ResponseEntity<>("Cart is empty", HttpStatus.BAD_REQUEST);
    }
}
