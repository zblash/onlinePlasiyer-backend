package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.order.OrderItemPDF;
import com.marketing.web.dtos.order.ReadableOrder;
import com.marketing.web.dtos.order.ReadableOrderItem;
import com.marketing.web.models.Barcode;
import com.marketing.web.models.CartItem;
import com.marketing.web.models.Order;
import com.marketing.web.models.OrderItem;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            orderItem.setProductSpecify(cartItem.getProduct());
            orderItem.setMerchant(cartItem.getProduct().getMerchant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            BigDecimal totalPrice = cartItem.getDiscountedTotalPrice().compareTo(BigDecimal.ZERO) > 0 ? cartItem.getDiscountedTotalPrice() : cartItem.getTotalPrice();
            orderItem.setDiscountedTotalPrice(totalPrice);
            orderItem.setCommission(totalPrice.multiply(BigDecimal.valueOf(cartItem.getProduct().getCommission() / 100)).doubleValue());
            return orderItem;
        }
    }

    public static ReadableOrder orderToReadableOrder(Order order) {
        if (order == null) {
            return null;
        } else {
            ReadableOrder readableOrder = new ReadableOrder();
            readableOrder.setId(order.getId().toString());
            readableOrder.setBuyerName(order.getCustomer().getUser().getName());
            readableOrder.setMerchant(UserMapper.merchantToCommonMerchant(order.getMerchant()));
            readableOrder.setOrderDate(order.getOrderDate());
            readableOrder.setWaybillDate(order.getWaybillDate());
            readableOrder.setTotalPrice(order.getOrderItems().stream().map(OrderItem::getDiscountedTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            readableOrder.setStatus(order.getStatus());
            readableOrder.setPaymentType(order.getPaymentType());
            readableOrder.setCommentable(order.isCommentable());
            readableOrder.setCommission(order.getOrderItems().stream().mapToDouble(OrderItem::getCommission).sum());
            readableOrder.setOrderItems(order.getOrderItems().stream()
                    .map(OrderMapper::orderItemToReadableOrderItem).collect(Collectors.toList()));
            readableOrder.setBuyerAddress(UserMapper.addressToReadableAddress(order.getCustomer().getUser().getCity(),order.getCustomer().getUser().getState(),order.getCustomer().getUser().getAddressDetails()));
            return readableOrder;
        }
    }

    public static ReadableOrderItem orderItemToReadableOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        } else {
            ReadableOrderItem readableOrderItem = new ReadableOrderItem();
            readableOrderItem.setId(orderItem.getId().toString());
            readableOrderItem.setPrice(orderItem.getPrice());
            readableOrderItem.setUnitPrice(orderItem.getUnitPrice());
            readableOrderItem.setUnitType(orderItem.getUnitType());
            readableOrderItem.setRecommendedRetailPrice(orderItem.getRecommendedRetailPrice());
            readableOrderItem.setProductName(orderItem.getProduct().getName());
            readableOrderItem.setProductBarcodeList(orderItem.getProduct().getBarcodes().stream().map(Barcode::getBarcodeNo).collect(Collectors.toList()));
            readableOrderItem.setProductPhotoUrl(orderItem.getProduct().getPhotoUrl());
            readableOrderItem.setProductTax(orderItem.getProduct().getTax());
            readableOrderItem.setMerchant(UserMapper.merchantToCommonMerchant(orderItem.getMerchant()));
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

    public static OrderItemPDF orderItemToOrderItemPDF(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderItemPDF orderItemPDF = new OrderItemPDF();
        orderItemPDF.setBarcode(orderItem.getProduct().getBarcodes().stream().findFirst().get().getBarcodeNo());
        if (orderItem.getTotalPrice().compareTo(orderItem.getDiscountedTotalPrice()) == 0) {
            orderItemPDF.setDiscountPrice(BigDecimal.ZERO);
        } else {
            orderItemPDF.setDiscountPrice(orderItem.getTotalPrice().subtract(orderItem.getDiscountedTotalPrice()));
        }
        orderItemPDF.setProductName(orderItem.getProduct().getName());
        orderItemPDF.setQuantity(orderItem.getQuantity());
        orderItemPDF.setUnitPrice(orderItem.getUnitPrice());
        orderItemPDF.setUnitType(orderItem.getUnitType().toString());
        orderItemPDF.setTotalPrice(orderItem.getTotalPrice());
        return orderItemPDF;
    }
}
