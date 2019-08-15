package com.marketing.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {

    @NotBlank
    @Size(min = 3,max = 20)
    private String name;

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
    private double tax;

    @NotNull
    private double recommendedRetailPrice;
}
