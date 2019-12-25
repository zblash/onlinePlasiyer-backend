package com.marketing.web.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableBarcode implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    @Size(min = 8,max = 13, message = "{validation.size}")
    private String barcode;

}
