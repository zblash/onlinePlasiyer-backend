package com.marketing.web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.models.UnitType;
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

    @NotNull
    private Long categoryId;

    @NotBlank
    @Size(min = 3,max = 20)
    private String name;

    @NotBlank
    @Size(min = 10,max = 100)
    private String barcode;

    @NotNull
    private double tax;

    @JsonIgnore
    private String photoUrl;

}
