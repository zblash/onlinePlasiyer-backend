package com.marketing.web.dtos.cart;

import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCartItem implements Serializable {

    private String id;

    private double productPrice;

    private double unitPrice;

    private UnitType unitType;

    private double recommendedRetailPrice;

    private String productName;

    private String productBarcode;

    private double productTax;

    private String productPhotoUrl;

    private String sellerName;

    private int quantity;

    private double totalPrice;

}
