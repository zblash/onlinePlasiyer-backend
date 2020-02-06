package com.marketing.web.dtos.payments;

import com.marketing.web.enums.PaymentOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadablePaymentMethods implements Serializable {

    private String id;

    private String displayName;

    private PaymentOption paymentOption;

}
