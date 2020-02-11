package com.marketing.web.dtos.obligation;

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
public class ReadableObligationActivity implements Serializable {

    private String id;

    private double price;

    private Long documentNo;

    private double totalDebt;

    private double totalReceivable;

    private CreditActivityType obligationActivityType;

    private String userId;

    private String userName;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date date;
}
