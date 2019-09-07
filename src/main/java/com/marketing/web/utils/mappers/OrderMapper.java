package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.WritableOrder;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import org.aspectj.weaver.ast.Or;
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

    default ReadableOrder orderToReadableOrder(Order order){
        ReadableOrder readableOrder = new ReadableOrder();
        readableOrder.setId(order.getUuid().toString());
        readableOrder.setBuyerName(order.getBuyer().getName());
        readableOrder.setSellerName(order.getSeller().getName());
        readableOrder.setOrderDate(order.getOrderDate());
        readableOrder.setWaybillDate(order.getWaybillDate());
        readableOrder.setTotalPrice(order.getTotalPrice());
        readableOrder.setStatus(order.getStatus());
        readableOrder.setOrderItems(order.getOrderItems());
        return readableOrder;
    }

}
