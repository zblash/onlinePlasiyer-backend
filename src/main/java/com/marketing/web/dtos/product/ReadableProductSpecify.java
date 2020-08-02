package com.marketing.web.dtos.product;

import com.marketing.web.dtos.user.readable.CommonMerchant;
import com.marketing.web.dtos.user.readable.ReadableState;
import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableProductSpecify implements Serializable {

    private String id;

    private BigDecimal totalPrice;

    private BigDecimal unitPrice;

    private int quantity;

    private double contents;

    private UnitType unitType;

    private BigDecimal recommendedRetailPrice;

    private double commission;

    private String productId;

    private String productName;

    private CommonMerchant merchant;

    private List<ReadableState> states;

    private List<String> productBarcodeList;

    private boolean discount;

    private ReadablePromotion promotion;

}
