package com.marketing.web.dtos.product;

import com.marketing.web.dtos.DTO;
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

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableProductSpecify extends DTO {

    @NotBlank
    @Size(min = 13,max = 100)
    private String barcode;

    @NotNull
    private double totalPrice;

    @NotNull
    private double unitPrice;

    @NotNull
    private int quantity;

    @NotNull
    private double contents;

    @NotNull
    private UnitType unitType;

    @NotNull
    private double recommendedRetailPrice;

    @NotNull
    private List<String> stateList;
}
