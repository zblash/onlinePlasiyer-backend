package com.marketing.web.dtos.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCartItemDetail implements Serializable {

    private String id;

    private String sellerId;

    private String sellerName;

    private int quantity;

    private double totalPrice;

    private List<ReadableCartItem> details;
}
