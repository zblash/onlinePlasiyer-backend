package com.marketing.web.dtos.credit;

import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableCreditActivity implements Serializable {

    private String id;

    private double price;

    private CreditActivityType creditActivityType;

    private CreditType creditType;

    private String customerId;

    private String customerName;

    private String merchantId;

    private String merchantName;

}
