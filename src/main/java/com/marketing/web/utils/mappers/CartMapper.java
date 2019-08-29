package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.cart.CartDTO;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartMapper INSTANCE = Mappers.getMapper( CartMapper.class );

    default CartDTO cartToCartDTO(Cart cart){
        List<CartItem> items = cart.getItems();
        double totalPrice = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        int quantity = items.stream().mapToInt(CartItem::getQuantity).sum();
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(cart.getItems());
        cartDTO.setTotalPrice(totalPrice);
        cartDTO.setQuantity(quantity);
        return cartDTO;
    }
}
