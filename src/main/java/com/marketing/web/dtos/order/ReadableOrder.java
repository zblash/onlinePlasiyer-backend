package com.marketing.web.dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.marketing.web.dtos.user.readable.CommonMerchant;
import com.marketing.web.dtos.user.readable.ReadableAddress;
import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.PaymentOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableOrder implements Serializable {

    private String id;

    private Long code;

    private BigDecimal totalPrice;

    private OrderStatus status;

    private boolean commentable;

    private PaymentOption paymentType;

    private double commission;

    private CommonMerchant merchant;

    private String buyerName;

    private LocalDate orderDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate waybillDate;

    private ReadableAddress buyerAddress;

    private List<ReadableOrderItem> orderItems;
}
