package com.marketing.web.dtos.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCartItemDetail implements Serializable {

    private String id;

    private String sellerId;

    private String sellerName;

    private int quantity;

    private BigDecimal totalPrice;

    private BigDecimal discountedTotalPrice;

    private List<ReadableCartItem> details;
}
