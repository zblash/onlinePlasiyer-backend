package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.ReadableOrderItem;
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
            orderItem.setDiscountedTotalPrice(cartItem.getDiscountedTotalPrice());
            double totalPrice = cartItem.getDiscountedTotalPrice() > 0 ? cartItem.getDiscountedTotalPrice() : cartItem.getTotalPrice();
            orderItem.setCommission(totalPrice * cartItem.getProduct().getCommission());
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
            readableOrder.setDiscountedTotalPrice(order.getDiscountedTotalPrice());
            readableOrder.setStatus(order.getStatus());
            readableOrder.setCommission(order.getCommission());
            readableOrder.setOrderItems(order.getOrderItems().stream()
                    .map(OrderMapper::orderItemToReadableOrderItem).collect(Collectors.toList()));
            readableOrder.setBuyerAddress(UserMapper.addressToReadableAddress(order.getBuyer().getAddress()));
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
            readableOrderItem.setDiscountedTotalPrice(orderItem.getDiscountedTotalPrice());
            return readableOrderItem;
        }
    }

    public static WrapperPagination<ReadableOrder> pagedOrderListToWrapperReadableOrder(Page<Order> pagedOrder) {
        if (pagedOrder == null) {
            return null;
        } else {
            WrapperPagination<ReadableOrder> wrapperReadableOrder = new WrapperPagination<>();
            wrapperReadableOrder.setKey("orders");
            wrapperReadableOrder.setTotalPage(pagedOrder.getTotalPages());
            wrapperReadableOrder.setPageNumber(pagedOrder.getNumber() + 1);
            if (pagedOrder.hasPrevious()) {
                wrapperReadableOrder.setPreviousPage(pagedOrder.getNumber());
            }
            if (pagedOrder.hasNext()) {
                wrapperReadableOrder.setNextPage(pagedOrder.getNumber() + 2);
            }
            wrapperReadableOrder.setFirst(pagedOrder.isFirst());
            wrapperReadableOrder.setLast(pagedOrder.isLast());
            wrapperReadableOrder.setElementCountOfPage(pagedOrder.getNumberOfElements());
            wrapperReadableOrder.setTotalElements(pagedOrder.getTotalElements());
            wrapperReadableOrder.setValues(pagedOrder.getContent().stream()
                    .map(OrderMapper::orderToReadableOrder).collect(Collectors.toList()));
            return wrapperReadableOrder;
        }
    }
}
