package com.marketing.web.dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableOrder implements Serializable {

    private double paidPrice;

    @NotNull(message = "{validation.notNull}")
    private OrderStatus status;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date waybillDate;

    private PaymentType paymentType;
}
