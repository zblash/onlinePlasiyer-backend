package com.marketing.web.dtos;

import com.marketing.web.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO extends DTO {

    private double totalPrice;

    private OrderStatus status;

    private String sellerName;

    private String buyerName;

    private Date orderDate;

    private Date waybillDate;
}
