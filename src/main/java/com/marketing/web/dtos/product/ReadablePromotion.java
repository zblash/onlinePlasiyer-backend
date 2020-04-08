package com.marketing.web.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadablePromotion implements Serializable {

    private String promotionText;

    private double discountValue;

    private int discountUnit;

}
