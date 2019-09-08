package com.marketing.web.dtos.product;

import com.marketing.web.dtos.DTO;
import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableProductSpecify extends DTO {

    private String id;

    private double totalPrice;

    private double unitPrice;

    private int quantity;

    private double contents;

    private UnitType unitType;

    private double recommendedRetailPrice;

    private String productName;

    private String sellerName;

    private List<String> states;

}
