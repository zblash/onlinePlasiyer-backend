package com.marketing.web.dtos.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCredit implements Serializable {

    private String id;

    private double totalDebt;

    private double creditLimit;

    private String customerId;

    private String customerName;
}
