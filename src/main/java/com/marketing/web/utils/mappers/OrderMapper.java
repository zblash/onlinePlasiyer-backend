package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.ReadableOrderItem;
import com.marketing.web.dtos.order.WrapperReadableOrder;
import com.marketing.web.models.Barcode;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import org.springframework.data.domain.Page;

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
            orderItem.setProduct(cartItem.getProduct().getProduct());
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
            readableOrderItem.setProductName(orderItem.getProduct().getName());
            readableOrderItem.setProductBarcodeList(orderItem.getProduct().getBarcodes().stream().map(Barcode::getBarcodeNo).collect(Collectors.toList()));
            readableOrderItem.setProductPhotoUrl(orderItem.getProduct().getPhotoUrl());
            readableOrderItem.setProductTax(orderItem.getProduct().getTax());
            readableOrderItem.setSellerName(orderItem.getSeller().getName());
            readableOrderItem.setQuantity(orderItem.getQuantity());
            readableOrderItem.setTotalPrice(orderItem.getTotalPrice());
            return readableOrderItem;
        }
    }

    public static WrapperReadableOrder pagedOrderListToWrapperReadableOrder(Page<Order> pagedOrder){
        if (pagedOrder == null) {
            return null;
        } else {
            WrapperReadableOrder wrapperReadableOrder = new WrapperReadableOrder();
            wrapperReadableOrder.setKey("orders");
            wrapperReadableOrder.setTotalPage(pagedOrder.getTotalPages());
            wrapperReadableOrder.setPageNumber(pagedOrder.getNumber()+1);
            if (pagedOrder.hasPrevious()) {
                wrapperReadableOrder.setPreviousPage(pagedOrder.getNumber());
            }
            if (pagedOrder.hasNext()) {
                wrapperReadableOrder.setNextPage(pagedOrder.getNumber()+2);
            }
            wrapperReadableOrder.setFirst(pagedOrder.isFirst());
            wrapperReadableOrder.setLast(pagedOrder.isLast());
            wrapperReadableOrder.setNumberOfElements(pagedOrder.getNumberOfElements());
            wrapperReadableOrder.setTotalElements(pagedOrder.getTotalElements());
            wrapperReadableOrder.setValues(pagedOrder.getContent().stream()
                    .map(OrderMapper::orderToReadableOrder).collect(Collectors.toList()));
            return wrapperReadableOrder;
        }
    }
}
