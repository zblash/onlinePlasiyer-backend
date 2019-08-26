package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.OrderDTO;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
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
        orderItem.setProductBarcode(cartItem.getProduct().getProduct().getBarcode());
        orderItem.setProductName(cartItem.getProduct().getProduct().getName());
        orderItem.setProductTax(cartItem.getProduct().getProduct().getTax());
        orderItem.setProductPhotoUrl(cartItem.getProduct().getProduct().getPhotoUrl());
        orderItem.setSeller(cartItem.getProduct().getUser());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setTotalPrice(cartItem.getTotalPrice());
        return orderItem;
    }

    default OrderDTO orderToOrderDTO(Order order){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName(order.getBuyer().getName());
        orderDTO.setSellerName(order.getSeller().getName());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setWaybillDate(order.getWaybillDate());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setStatus(order.getStatus());
        return orderDTO;
    }

}
