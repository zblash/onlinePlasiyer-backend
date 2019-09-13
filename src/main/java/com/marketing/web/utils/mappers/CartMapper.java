package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.cart.ReadableCart;
import com.marketing.web.dtos.cart.ReadableCartItem;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;

import java.util.List;
import java.util.stream.Collectors;

public final class CartMapper {

    public static ReadableCart cartToReadableCart(Cart cart) {
        if (cart == null) {
            return null;
        } else {
            List<CartItem> items = cart.getItems();
            double totalPrice = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
            int quantity = items.stream().mapToInt(CartItem::getQuantity).sum();
            ReadableCart readableCart = new ReadableCart();
            readableCart.setId(cart.getUuid().toString());
            if (!cart.getItems().isEmpty() || cart.getItems() != null) {
                readableCart.setItems(cart.getItems().stream()
                        .map(CartMapper::cartItemToReadableCartItem).collect(Collectors.toList()));
            }
            readableCart.setTotalPrice(totalPrice);
            readableCart.setQuantity(quantity);
            return readableCart;
        }
    }

    public static ReadableCartItem cartItemToReadableCartItem(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        } else {
            ReadableCartItem readableCartItem = new ReadableCartItem();
            readableCartItem.setId(cartItem.getUuid().toString());
            readableCartItem.setProductPrice(cartItem.getProduct().getTotalPrice());
            readableCartItem.setUnitPrice(cartItem.getProduct().getUnitPrice());
            readableCartItem.setUnitType(cartItem.getProduct().getUnitType());
            readableCartItem.setRecommendedRetailPrice(cartItem.getProduct().getRecommendedRetailPrice());
            readableCartItem.setProductName(cartItem.getProduct().getProduct().getName());
            readableCartItem.setProductBarcode(cartItem.getProduct().getProduct().getBarcode());
            readableCartItem.setProductPhotoUrl(cartItem.getProduct().getProduct().getPhotoUrl());
            readableCartItem.setProductTax(cartItem.getProduct().getProduct().getTax());
            readableCartItem.setSellerName(cartItem.getProduct().getUser().getName());
            readableCartItem.setQuantity(cartItem.getQuantity());
            readableCartItem.setTotalPrice(cartItem.getTotalPrice());
            return readableCartItem;
        }
    }
}
