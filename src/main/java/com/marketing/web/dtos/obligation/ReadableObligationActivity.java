package com.marketing.web.dtos.obligation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableObligationActivity implements Serializable {

    private String id;

    private BigDecimal price;

    private Long documentNo;

    private BigDecimal totalDebt;

    private BigDecimal totalReceivable;

    private CreditActivityType obligationActivityType;

    private String userId;

    private String userName;

    private String customerName;

    private BigDecimal orderTotalPrice;

    private double orderCommissionPrice;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date date;
}
