package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.cart.ReadableCart;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;

import java.util.List;

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
            readableCart.setItems(cart.getItems());
            readableCart.setTotalPrice(totalPrice);
            readableCart.setQuantity(quantity);
            return readableCart;
        }
    }
}
