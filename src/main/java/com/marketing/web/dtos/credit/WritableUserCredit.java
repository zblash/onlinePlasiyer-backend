package com.marketing.web.dtos.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritableUserCredit implements Serializable {

    private double totalDebt;

    @NotNull(message = "{validation.notNull}")
    private double creditLimit;

    private String customerId;

    private String merchantId;
}
