package com.marketing.web.dtos.product;

import com.marketing.web.enums.PromotionType;
import com.marketing.web.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableProductSpecify implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    @Size(min = 13,max = 13, message = "{validation.size}")
    private String barcode;

    @NotNull(message = "{validation.notNull}")
    private double totalPrice;

    @NotNull(message = "{validation.notNull}")
    private double unitPrice;

    @NotNull(message = "{validation.notNull}")
    private int quantity;

    @NotNull(message = "{validation.notNull}")
    private double contents;

    @NotNull(message = "{validation.notNull}")
    private UnitType unitType;

    @NotNull(message = "{validation.notNull}")
    private double recommendedRetailPrice;

    @NotNull(message = "{validation.notNull}")
    private List<String> stateList;

    @NotNull(message = "{validation.notNull}")
    private boolean discount;

    private double discountValue;

    private int discountUnit;

    private String promotionText;
}
