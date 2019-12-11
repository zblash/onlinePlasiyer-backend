package com.marketing.web.dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marketing.web.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableOrder implements Serializable {

    private double paidPrice;

    private double discount;

    @NotBlank
    private OrderStatus status;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date waybillDate;
}
