package com.marketing.web.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemPDF implements Serializable {

    private String barcode;

    private String productName;

    private int quantity;

    private String unitType;

    private BigDecimal unitPrice;

    private BigDecimal discountPrice;

    private BigDecimal totalPrice;

}
