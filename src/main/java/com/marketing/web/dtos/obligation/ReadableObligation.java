package com.marketing.web.dtos.obligation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableObligation implements Serializable {

    private String id;

    private BigDecimal debt;

    private BigDecimal receivable;

    private String userId;

    private String userName;

}
