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
import com.marketing.web.services.user.CustomerService;
import com.marketing.web.services.user.MerchantService;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.facade.OrderFacade;
import com.marketing.web.utils.mappers.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/private/cart")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CartController {

    private final CartItemService cartItemService;

    private final CartItemHolderService cartItemHolderService;

    private final CartService cartService;

    private final MerchantService merchantService;

    private final UserService userService;

    private final ProductSpecifyService productSpecifyService;

    private final OrderFacade orderFacade;

    private final CreditService creditService;

    private final CustomerService customerService;

    private final Logger logger = LoggerFactory.getLogger(CartController.class);

    public CartController(CartItemService cartItemService, CartItemHolderService cartItemHolderService, CartService cartService, MerchantService merchantService, UserService userService, ProductSpecifyService productSpecifyService, OrderFacade orderFacade, CreditService creditService, CustomerService customerService) {
        this.cartItemService = cartItemService;
        this.cartItemHolderService = cartItemHolderService;
        this.cartService = cartService;
        this.merchantService = merchantService;
        this.userService = userService;
        this.productSpecifyService = productSpecifyService;
        this.orderFacade = orderFacade;
        this.creditService = creditService;
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<ReadableCart> getCart() {
        Customer customer = customerService.getLoggedInCustomer();
        Cart cart = cartService.findByCustomer(customer);
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cart));
    }

    @PostMapping
    public ResponseEntity<ReadableCart> addItem(@Valid @RequestBody WritableCartItem writableCartItem) {
        Customer customer = customerService.findByUser(userService.getLoggedInUser());
        Cart cart = cartService.findByCustomer(customer);
        if (writableCartItem.getQuantity() > 0) {
            ProductSpecify productSpecify = productSpecifyService.findById(writableCartItem.getProductId());
            if (productSpecify.getStates().contains(customer.getUser().getState())) {
                CartItemHolder cartItemHolder = cartItemHolderService.findByCartAndMerchant(cart, productSpecify.getMerchant().getId().toString())
                        .orElseGet(() -> cartItemHolderService.create(CartItemHolder.builder().cart(cart).merchantId(productSpecify.getMerchant().getId().toString()).merchantName(productSpecify.getMerchant().getUser().getName()).build()));
                CartItem cartItem = cartItemService.createOrUpdate(cartItemHolder, writableCartItem.getQuantity(), productSpecify);
                cartItemHolder.addCartItem(cartItem);

                return ResponseEntity.ok(CartMapper.cartToReadableCart(cart));
            }
            throw new BadRequestException("You can't order this product");
        }
        throw new BadRequestException("Quantity must bigger than 0");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableCart> removeItem(@PathVariable String id) {
        Customer customer = customerService.getLoggedInCustomer();
        Cart cart = cartService.findByCustomer(customer);
        CartItem cartItem = cartItemService.findById(id);
        if (cartItem.getCartItemHolder().getCartItems().size() == 1) {
            cartItemHolderService.delete(cart, cartItem.getCartItemHolder());
        } else {
            cartItem.getCartItemHolder().removeCartItem(cartItem);
            cartItemService.delete(cart, cartItemService.findById(id));
        }
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.findByCustomer(customer)));
    }


    @DeleteMapping
    public ResponseEntity<ReadableCart> clearCart() {
        Customer customer = customerService.getLoggedInCustomer();
        Cart cart = cartService.findByCustomer(customer);
        cartItemHolderService.deleteAll(cart.getItems());
        cart.setItems(new HashSet<>());
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cart));
    }

    @PostMapping("/checkout")
    public ResponseEntity<List<ReadableOrder>> checkout(@Valid @RequestBody WritableCheckout writableCheckout) {
        Customer customer = customerService.findByUser(userService.getLoggedInUser());
        Cart cart = cartService.findByCustomer(customer);

        if (!cart.getItems().isEmpty()
                && cart.getItems() != null
                && cart.getItems().stream()
                .filter(cartItemHolder -> writableCheckout.getSellerIdList().contains(cartItemHolder.getId().toString()))
                .allMatch(cartItemHolder -> cartItemHolder.getPaymentOption() != null)
                && CartStatus.PROCEED.equals(cart.getCartStatus())) {

            Set<CartItemHolder> cartItemHolderList = cart.getItems().stream()
                    .filter(cartItemHolder -> writableCheckout.getSellerIdList().contains(cartItemHolder.getId().toString()))
                    .collect(Collectors.toSet());

            List<ReadableOrder> order = orderFacade.checkoutCart(customer, cartItemHolderList, writableCheckout);

            cart.setCartStatus(CartStatus.NEW);
            cartService.update(cart.getId().toString(), cart);
            cartItemHolderService.deleteAll(cartItemHolderList);

            return ResponseEntity.ok(order);
        }
        throw new BadRequestException("Can not perform cart");
    }

    @PostMapping("/setPayment")
    public ResponseEntity<ReadableCart> setPayment(@Valid @RequestBody PaymentMethod paymentMethod) {
        Customer customer = customerService.findByUser(userService.getLoggedInUser());
        Cart cart = cartService.findByCustomer(customer);
        CartItemHolder cartItemHolder = cartItemHolderService.findByCartAndUuid(cart, paymentMethod.getHolderId());
        if (!PaymentOption.COD.equals(paymentMethod.getPaymentOption())) {
            Credit credit = paymentMethod.getPaymentOption().equals(PaymentOption.MERCHANT_CREDIT)
                    ? creditService.findByCustomerAndMerchant(customer, merchantService.findById(cartItemHolder.getMerchantId()))
                    .orElseThrow(() -> new BadRequestException("You have not credit from this merchant"))
                    : creditService.findSystemCreditByCustomer(customer);
            BigDecimal holderTotalPrice = cartItemHolder.getCartItems().stream().map(CartItem::getDiscountedTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (credit.getTotalDebt().add(holderTotalPrice).compareTo(credit.getCreditLimit()) > 0) {
                throw new BadRequestException("Insufficient credit");
            }
        }

        cartItemHolder.setPaymentOption(paymentMethod.getPaymentOption());
        cart.setCartStatus(CartStatus.PROCEED);
        return ResponseEntity.ok(CartMapper.cartToReadableCart(cartService.update(cart.getId().toString(), cart)));
    }

}
