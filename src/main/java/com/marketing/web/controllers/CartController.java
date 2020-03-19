package com.marketing.web.controllers;

import com.marketing.web.dtos.cart.PaymentMethod;
import com.marketing.web.dtos.cart.ReadableCart;
import com.marketing.web.dtos.cart.WritableCartItem;
import com.marketing.web.dtos.cart.WritableCheckout;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.enums.CartStatus;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.errors.BadRequestException;
import com.marketing.web.models.*;
import com.marketing.web.services.cart.CartItemHolderService;
import com.marketing.web.services.cart.CartItemService;
import com.marketing.web.services.cart.CartService;
import com.marketing.web.services.credit.CreditService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CartController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartItemHolderService cartItemHolderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductSpecifyService productSpecifyService;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private CreditService creditService;

    private Logger logger = LoggerFactory.getLogger(CartController.class);

    @GetMapping
    public ResponseEntity<ReadableCart> getCart() {
        User user = userService.getLoggedInUser();
        ReadableCart readableCart = CartMapper.cartToReadableCart(user.getCart());
        return ResponseEntity.ok(readableCart);
    }

    @PostMapping
    public ResponseEntity<ReadableCart> addItem(@Valid @RequestBody WritableCartItem writableCartItem) {
        User user = userService.getLoggedInUser();
        Cart cart = user.getCart();
        if (writableCartItem.getQuantity() > 0) {
            List<State> productStates = productSpecifyService.findByUUID(writableCartItem.getProductId()).getStates();
            if (productStates.contains(user.getState())) {
                ProductSpecify productSpecify = productSpecifyService.findByUUID(writableCartItem.getProductId());
                CartItemHolder cartItemHolder = cartItemHolderService.findByCartAndSeller(cart, productSpecify.getUser().getUuid().toString())
                        .orElseGet(() -> cartItemHolderService.create(CartItemHolder.builder().cart(cart).sellerId(productSpecify.getUser().getUuid().toString()).sellerName(productSpecify.getUser().getName()).build()));
                CartItem cartItem = cartItemService.createOrUpdate(cartItemHolder, writableCartItem.getQuantity(), productSpecify);
                cartItemHolder.addCartItem(cartItem);

                return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
            }
            throw new BadRequestException("You can't order this product");
        }
        throw new BadRequestException("Quantity must bigger than 0");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableCart> removeItem(@PathVariable String id) {
        User user = userService.getLoggedInUser();
        Cart cart = user.getCart();
        CartItem cartItem = cartItemService.findByUUID(id);
        if (cartItem.getCartItemHolder().getCartItems().size() == 1) {
            cartItemHolderService.delete(cart, cartItem.getCartItemHolder());
        } else {
            cartItem.getCartItemHolder().removeCartItem(cartItem);
            cartItemService.delete(cart, cartItemService.findByUUID(id));
        }
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
    }


    @DeleteMapping
    public ResponseEntity<ReadableCart> clearCart() {
        User user = userService.getLoggedInUser();
        cartItemHolderService.deleteAll(user.getCart().getItems());
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByUser(user)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<List<ReadableOrder>> checkout(@Valid @RequestBody WritableCheckout writableCheckout) {
        User user = userService.getLoggedInUser();
        Cart cart = user.getCart();

        if (!cart.getItems().isEmpty()
                && cart.getItems() != null
                && cart.getItems().stream()
                .filter(cartItemHolder -> writableCheckout.getSellerIdList().contains(cartItemHolder.getUuid().toString()))
                .allMatch(cartItemHolder -> cartItemHolder.getPaymentOption() != null)
                && CartStatus.PROCEED.equals(cart.getCartStatus())) {

            Set<CartItemHolder> cartItemHolderList = cart.getItems().stream()
                    .filter(cartItemHolder -> writableCheckout.getSellerIdList().contains(cartItemHolder.getUuid().toString()))
                    .collect(Collectors.toSet());

            List<ReadableOrder> order = orderFacade.checkoutCart(user, cartItemHolderList, writableCheckout);

            cart.setCartStatus(CartStatus.NEW);
            cartService.update(cart.getId(), cart);
            cartItemHolderService.deleteAll(cartItemHolderList);

            return ResponseEntity.ok(order);
        }
        throw new BadRequestException("Can not perform cart");
    }

    @PostMapping("/setPayment")
    public ResponseEntity<ReadableCart> setPayment(@Valid @RequestBody PaymentMethod paymentMethod) {
        User loggedInUser = userService.getLoggedInUser();
        Cart cart = loggedInUser.getCart();
        CartItemHolder cartItemHolder = cartItemHolderService.findByCartAndUuid(cart, paymentMethod.getHolderId());
        if (PaymentOption.MERCHANT_CREDIT.equals(paymentMethod.getPaymentOption())) {
            Credit credit = creditService.findByCustomerAndMerchant(loggedInUser, userService.findByUUID(cartItemHolder.getSellerId()))
                    .orElseThrow(() -> new BadRequestException("You have not credit from this merchant"));
            double holderTotalPrice = cartItemHolder.getCartItems().stream().mapToDouble(CartItem::getDiscountedTotalPrice).sum();
            if (credit.getTotalDebt() + holderTotalPrice > credit.getCreditLimit()) {
                throw new BadRequestException("Insufficient credit");
            }
        }
        cartItemHolder.setPaymentOption(paymentMethod.getPaymentOption());
        cart.setCartStatus(CartStatus.PROCEED);
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.update(cart.getId(), cart)));
    }

}
