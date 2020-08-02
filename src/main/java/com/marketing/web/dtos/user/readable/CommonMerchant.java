package com.marketing.web.dtos.user.readable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonMerchant implements Serializable {

    private String merchantId;
    private String merchantName;
    private double merchantScore;

}
