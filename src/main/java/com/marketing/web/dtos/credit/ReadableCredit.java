package com.marketing.web.dtos.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCredit implements Serializable {

    private String id;

    private BigDecimal totalDebt;

    private BigDecimal creditLimit;

    private String customerId;

    private String customerName;
}
