package com.marketing.web.dtos.cart;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableCartItem implements Serializable {

    @NotNull
    private String productId;

    @NotNull
    private int quantity;

}
