package com.marketing.web.dtos.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCart implements Serializable {

    private String id;

    private int quantity;

    private double totalPrice;

    private double discountedTotalPrice;

    private List<ReadableCartItemDetail> items;
}
