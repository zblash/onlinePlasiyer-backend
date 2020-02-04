package com.marketing.web.dtos.product;

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
public class WritableProduct implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String categoryId;

    @NotBlank(message = "{validation.notBlank}")
    private String name;

    @NotBlank(message = "{validation.notBlank}")
    @Size(min = 13,max = 13, message = "{validation.size}")
    private String barcode;

    @NotNull(message = "{validation.notNull}")
    private double tax;

    private boolean status;

    private Double commission;
}
