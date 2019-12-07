package com.marketing.web.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummary implements Serializable {

    private String id;

    private int newCount;

    private int finishedCount;

    private int cancelledCount;

    private int paidCount;

}
