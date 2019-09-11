package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.ReadableOrderItem;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;

import java.util.stream.Collectors;

public final class OrderMapper {

    public static OrderItem cartItemToOrderItem(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        } else {
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
    }

    public static ReadableOrder orderToReadableOrder(Order order) {
        if (order == null) {
            return null;
        } else {
            ReadableOrder readableOrder = new ReadableOrder();
            readableOrder.setId(order.getUuid().toString());
            readableOrder.setBuyerName(order.getBuyer().getName());
            readableOrder.setSellerName(order.getSeller().getName());
            readableOrder.setOrderDate(order.getOrderDate());
            readableOrder.setWaybillDate(order.getWaybillDate());
            readableOrder.setTotalPrice(order.getTotalPrice());
            readableOrder.setStatus(order.getStatus());
            readableOrder.setOrderItems(order.getOrderItems().stream()
                    .map(OrderMapper::orderItemToReadableOrderItem).collect(Collectors.toList()));
            return readableOrder;
        }
    }

    public static ReadableOrderItem orderItemToReadableOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        } else {
            ReadableOrderItem readableOrderItem = new ReadableOrderItem();
            readableOrderItem.setId(orderItem.getUuid().toString());
            readableOrderItem.setPrice(orderItem.getPrice());
            readableOrderItem.setUnitPrice(orderItem.getUnitPrice());
            readableOrderItem.setUnitType(orderItem.getUnitType());
            readableOrderItem.setRecommendedRetailPrice(orderItem.getRecommendedRetailPrice());
            readableOrderItem.setProductName(orderItem.getProductName());
            readableOrderItem.setProductBarcode(orderItem.getProductBarcode());
            readableOrderItem.setProductPhotoUrl("http://localhost:8080/photos/" + orderItem.getProductPhotoUrl());
            readableOrderItem.setProductTax(orderItem.getProductTax());
            readableOrderItem.setSellerName(orderItem.getSeller().getName());
            readableOrderItem.setQuantity(orderItem.getQuantity());
            readableOrderItem.setTotalPrice(orderItem.getTotalPrice());
            return readableOrderItem;
        }
    }
}
