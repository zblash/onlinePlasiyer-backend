package com.marketing.web.dtos.order;

import com.marketing.web.dtos.user.readable.CommonMerchant;
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
public class ReadableOrderItem implements Serializable {

    private String id;

    private BigDecimal price;

    private BigDecimal unitPrice;

    private UnitType unitType;

    private BigDecimal recommendedRetailPrice;

    private String productName;

    private List<String> productBarcodeList;

    private double productTax;

    private String productPhotoUrl;

    private CommonMerchant merchant;

    private int quantity;

    private BigDecimal totalPrice;

    private BigDecimal discountedTotalPrice;

}
