package com.marketing.web.dtos.order;

import com.marketing.web.dtos.DTO;
import com.marketing.web.enums.UnitType;
import com.marketing.web.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableOrderItem extends DTO {

    private String id;

    private double price;

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
