package com.marketing.web.dtos.order;

import com.marketing.web.dtos.DTO;
import com.marketing.web.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableOrder extends DTO {

    private double paidPrice;

    private double unPaidPrice;

    private double discount;

    @NotNull
    private OrderStatus status;

    private Date waybillDate;
}
