package com.marketing.web.dtos.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WritableUserCredit implements Serializable {

    private BigDecimal totalDebt;

    @NotNull(message = "{validation.notNull}")
    private BigDecimal creditLimit;

    private String customerId;

    private String merchantId;
}
