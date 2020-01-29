package com.marketing.web.dtos.cart;

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
public class ReadableCartItem implements Serializable {

    private String id;

    private double productPrice;

    private String productId;

    private double unitPrice;

    private UnitType unitType;

    private double recommendedRetailPrice;

    private String productName;

    private List<String> productBarcodeList;

    private double productTax;

    private String productPhotoUrl;

    private String sellerName;

    private int quantity;

    private double discountedTotalPrice;

    private double totalPrice;

    private boolean isDiscounted;

    private String discountText;

}
