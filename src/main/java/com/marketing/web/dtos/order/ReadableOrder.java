package com.marketing.web.dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marketing.web.dtos.user.ReadableAddress;
import com.marketing.web.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableOrder implements Serializable {

    private String id;

    private Long code;

    private double totalPrice;

    private OrderStatus status;

    private double commission;

    private String sellerName;

    private String buyerName;

    private Date orderDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date waybillDate;

    private ReadableAddress buyerAddress;

    private List<ReadableOrderItem> orderItems;
}
