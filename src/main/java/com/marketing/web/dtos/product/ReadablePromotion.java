package com.marketing.web.dtos.product;

import com.marketing.web.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadablePromotion implements Serializable {

    private String promotionText;

    private BigDecimal discountValue;

    private int discountUnit;

}
