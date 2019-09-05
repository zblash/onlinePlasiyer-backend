package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.cart.ReadableCart;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper( CartMapper.class );

    default ReadableCart cartToReadableCart(Cart cart){
        List<CartItem> items = cart.getItems();
        double totalPrice = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        int quantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        ReadableCart readableCart = new ReadableCart();
        readableCart.setItems(cart.getItems());
        readableCart.setTotalPrice(totalPrice);
        readableCart.setQuantity(quantity);
        return readableCart;
    }
}
