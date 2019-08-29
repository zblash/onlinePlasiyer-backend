package com.marketing.web.dtos.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO extends DTO {

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

    private boolean status;
}
