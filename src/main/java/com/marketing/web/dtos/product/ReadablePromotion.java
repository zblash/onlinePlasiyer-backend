package com.marketing.web.dtos.product;

import com.marketing.web.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadablePromotion implements Serializable {

    private String promotionText;

    private PromotionType promotionType;

    private double discountPercent;

    private int discountUnit;

}
