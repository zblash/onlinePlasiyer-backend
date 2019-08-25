package com.marketing.web.dtos;

import com.marketing.web.models.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecifyDTO implements Serializable {

    @NotBlank
    @Size(min = 10,max = 100)
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

    private String city;

    private List<String> stateList;
}
