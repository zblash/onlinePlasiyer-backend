package com.marketing.web.dtos.order;

import com.marketing.web.dtos.DTO;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.models.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableOrder extends DTO {

    private Long id;

    private double totalPrice;

    private OrderStatus status;

    private String sellerName;

    private String buyerName;

    private Date orderDate;

    private Date waybillDate;

    private List<OrderItem> orderItems;
}
