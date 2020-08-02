package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.cart.ReadableCart;
import com.marketing.web.dtos.cart.ReadableCartItem;
import com.marketing.web.dtos.cart.ReadableCartItemDetail;
import com.marketing.web.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class CartMapper {

    private final static Logger logger = LoggerFactory.getLogger(CartMapper.class);
    public static ReadableCart cartToReadableCart(Cart cart) {
        if (cart == null) {
            return null;
        } else {
            Set<CartItemHolder> holderItems = cart.getItems();
            List<CartItem> items = new ArrayList<>();
            holderItems.stream().map(CartItemHolder::getCartItems).forEach(items::addAll);

            BigDecimal totalPrice = items.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal discountedTotalPrice = items.stream().map(CartItem::getDiscountedTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            int quantity = items.stream().mapToInt(CartItem::getQuantity).sum();
            ReadableCart readableCart = new ReadableCart();
            readableCart.setId(cart.getId().toString());

            readableCart.setItems(holderItems.stream().map(CartMapper::cartItemHolderToReadableCartItemDetail).collect(Collectors.toList()));
            readableCart.setDiscountedTotalPrice(discountedTotalPrice);
            readableCart.setTotalPrice(totalPrice);
            readableCart.setQuantity(quantity);
            return readableCart;
        }
    }

    public static ReadableCartItem cartItemToReadableCartItem(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        } else {
            ReadableCartItem readableCartItem = new ReadableCartItem();
            readableCartItem.setId(cartItem.getId().toString());
            readableCartItem.setProductId(cartItem.getProduct().getId().toString());
            readableCartItem.setProductPrice(cartItem.getProduct().getTotalPrice());
            readableCartItem.setUnitContents(cartItem.getProduct().getContents());
            readableCartItem.setUnitPrice(cartItem.getProduct().getUnitPrice());
            readableCartItem.setUnitType(cartItem.getProduct().getUnitType());
            readableCartItem.setRecommendedRetailPrice(cartItem.getProduct().getRecommendedRetailPrice());
            readableCartItem.setProductName(cartItem.getProduct().getProduct().getName());
            readableCartItem.setProductBarcodeList(cartItem.getProduct().getProduct().getBarcodes().stream().map(Barcode::getBarcodeNo).collect(Collectors.toList()));
            readableCartItem.setProductPhotoUrl(cartItem.getProduct().getProduct().getPhotoUrl());
            readableCartItem.setProductTax(cartItem.getProduct().getProduct().getTax());
            readableCartItem.setMerchant(UserMapper.merchantToCommonMerchant(cartItem.getProduct().getMerchant()));
            readableCartItem.setQuantity(cartItem.getQuantity());
            readableCartItem.setTotalPrice(cartItem.getTotalPrice());
            readableCartItem.setDiscountedTotalPrice(cartItem.getDiscountedTotalPrice());
            boolean isDiscounted = cartItem.getPromotion() != null;
            readableCartItem.setDiscounted(isDiscounted);
            if (isDiscounted) {
                readableCartItem.setDiscountText(cartItem.getPromotion().getPromotionText());
            }
            return readableCartItem;
        }
    }

    public static ReadableCartItemDetail cartItemHolderToReadableCartItemDetail(CartItemHolder cartItemHolder) {
        if (cartItemHolder == null) {
            return null;
        } else {
            ReadableCartItemDetail readableCartItemDetail = new ReadableCartItemDetail();
            readableCartItemDetail.setId(cartItemHolder.getId().toString());
            readableCartItemDetail.setQuantity(cartItemHolder.getCartItems().stream().mapToInt(CartItem::getQuantity).sum());
            readableCartItemDetail.setTotalPrice(cartItemHolder.getCartItems().stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            readableCartItemDetail.setDiscountedTotalPrice(cartItemHolder.getCartItems().stream().map(CartItem::getDiscountedTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            readableCartItemDetail.setDetails(cartItemHolder.getCartItems().stream().map(CartMapper::cartItemToReadableCartItem).collect(Collectors.toList()));
            readableCartItemDetail.setSellerId(cartItemHolder.getMerchantId());
            readableCartItemDetail.setSellerName(cartItemHolder.getMerchantName());
            return readableCartItemDetail;
        }
    }
}
