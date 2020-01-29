package com.marketing.web.dtos.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableCheckout implements Serializable {

    @NotNull(message = "{validation.notNull}")
    private List<String> sellerIdList;

}
