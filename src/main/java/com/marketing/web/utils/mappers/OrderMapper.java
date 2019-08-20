package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.CategoryDTO;
import com.marketing.web.models.Cart;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Category;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper( OrderMapper.class );

    @InheritInverseConfiguration
    OrderItem cartItemToOrderItem(CartItem cartItem);

}
