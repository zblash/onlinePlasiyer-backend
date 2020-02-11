package com.marketing.web.dtos.cart;

import com.marketing.web.enums.PaymentOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod implements Serializable {

    @NotNull(message = "{validation.notNull}")
    private PaymentOption paymentOption;

    @NotNull(message = "{validation.notNull}")
    private String holderId;

}
