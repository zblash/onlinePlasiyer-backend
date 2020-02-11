package com.marketing.web.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableCommission implements Serializable {

    @NotNull(message = "{validation.notBlank}")
    private String id;

    @NotNull(message = "{validation.notNull}")
    private double commission;

}
