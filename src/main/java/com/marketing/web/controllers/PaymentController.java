package com.marketing.web.controllers;

import com.marketing.web.dtos.payments.ReadablePaymentMethods;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/private/payment")
public class PaymentController {

    private final MessageSource messageSource;

    public PaymentController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/methods")
    public ResponseEntity<List<ReadablePaymentMethods>> getMethods(Locale locale) {
        List<ReadablePaymentMethods> paymentMethodsList = new ArrayList<>();
        for (PaymentOption paymentOption :
                PaymentOption.values()) {
            ReadablePaymentMethods readablePaymentMethods = new ReadablePaymentMethods();
            readablePaymentMethods.setId(RandomStringGenerator.generateId());
            readablePaymentMethods.setPaymentOption(paymentOption);
            readablePaymentMethods.setDisplayName(messageSource.getMessage("payment.options."+paymentOption.toString().toLowerCase(), null, locale));
            paymentMethodsList.add(readablePaymentMethods);
        }

        return ResponseEntity.ok(paymentMethodsList);
    }

}
