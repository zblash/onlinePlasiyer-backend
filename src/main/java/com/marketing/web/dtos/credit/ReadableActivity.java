package com.marketing.web.dtos.credit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marketing.web.enums.ActivityType;
import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.enums.CreditType;
import com.marketing.web.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableActivity implements Serializable {

    private String id;

    private Long documentNo;

    private ActivityType activityType;

    private PaymentType paymentType;

    private BigDecimal price;

    private BigDecimal paidPrice;

    private BigDecimal currentDebt;

    private BigDecimal currentReceivable;

    private BigDecimal creditLimit;

    private String customerId;

    private String customerName;

    private String merchantId;

    private String merchantName;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate date;


}
