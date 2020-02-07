package com.marketing.web.dtos.order;

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
public class ReadableOrderItem implements Serializable {

    private String id;

    private double price;

    private double unitPrice;

    private UnitType unitType;

    private double recommendedRetailPrice;

    private String productName;

    private List<String> productBarcodeList;

    private double productTax;

    private String productPhotoUrl;

    private String sellerName;

    private int quantity;

    private double totalPrice;

}
