package com.marketing.web.dtos;

import com.marketing.web.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements Serializable {

    private double totalPrice;

    private OrderStatus status;

    private String sellerName;

    private String buyerName;

    private Date orderDate;

    private Date waybillDate;
}
