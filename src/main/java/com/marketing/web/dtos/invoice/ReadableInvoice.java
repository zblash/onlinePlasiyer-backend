package com.marketing.web.dtos.invoice;

import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableInvoice extends DTO {

    private String id;

    private double totalPrice;

    private double paidPrice;

    private double unPaidPrice;

    private double discount;

    private String seller;

    private String buyer;

}
