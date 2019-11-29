package com.marketing.web.dtos.invoice;

import com.marketing.web.dtos.order.ReadableOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableInvoice implements Serializable {

    private String id;

    private double totalPrice;

    private double paidPrice;

    private double unPaidPrice;

    private double discount;

    private String seller;

    private String buyer;

    private ReadableOrder order;

}
