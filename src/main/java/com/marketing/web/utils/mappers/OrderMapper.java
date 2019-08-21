package com.marketing.web.utils.mappers;

import com.marketing.web.models.CartItem;
import com.marketing.web.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper( OrderMapper.class );


    default OrderItem cartItemToOrderItem(CartItem cartItem){
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(cartItem.getProduct().getTotalPrice());
        orderItem.setUnitPrice(cartItem.getProduct().getUnitPrice());
        orderItem.setUnitType(cartItem.getProduct().getUnitType());
        orderItem.setRecommendedRetailPrice(cartItem.getProduct().getRecommendedRetailPrice());
        orderItem.setProduct(cartItem.getProduct().getProduct());
        orderItem.setSeller(cartItem.getProduct().getUser());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setTotalPrice(cartItem.getTotalPrice());
        return orderItem;
    }

}
