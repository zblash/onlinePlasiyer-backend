package com.marketing.web.dtos.credit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCreditActivity implements Serializable {

    private String id;

    private Long documentNo;

    private double price;

    private double creditLimit;

    private double totalDebt;

    private CreditActivityType creditActivityType;

    private CreditType creditType;

    private String customerId;

    private String customerName;

    private String merchantId;

    private String merchantName;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date date;

}
