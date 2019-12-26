package com.marketing.web.dtos.payments;

import com.marketing.web.enums.PaymentOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadablePaymentMethods implements Serializable {

    private String id;

    private List<PaymentOption> paymentOptions;

}
