package com.marketing.web.dtos.product;

import com.marketing.web.dtos.user.ReadableState;
import com.marketing.web.enums.PromotionType;
import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableProductSpecify implements Serializable {

    private String id;

    private double totalPrice;

    private double unitPrice;

    private int quantity;

    private double contents;

    private UnitType unitType;

    private double recommendedRetailPrice;

    private double commission;

    private String productId;

    private String productName;

    private String sellerName;

    private List<ReadableState> states;

    private List<String> productBarcodeList;

    private boolean discount;

    private ReadablePromotion promotion;

}
