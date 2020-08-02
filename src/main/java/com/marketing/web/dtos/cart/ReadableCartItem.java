package com.marketing.web.dtos.cart;

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
public class ReadableCartItem implements Serializable {

    private String id;

    private BigDecimal productPrice;

    private String productId;

    private double unitContents;

    private BigDecimal unitPrice;

    private UnitType unitType;

    private BigDecimal recommendedRetailPrice;

    private String productName;

    private List<String> productBarcodeList;

    private double productTax;

    private String productPhotoUrl;

    private CommonMerchant merchant;

    private int quantity;

    private BigDecimal discountedTotalPrice;

    private BigDecimal totalPrice;

    private boolean isDiscounted;

    private String discountText;

}
