package com.marketing.web.dtos.obligation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableObligation implements Serializable {

    private BigDecimal debt;

    private BigDecimal receivable;

}
