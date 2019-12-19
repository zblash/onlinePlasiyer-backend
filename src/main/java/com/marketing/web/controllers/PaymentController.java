package com.marketing.web.controllers;

import com.marketing.web.dtos.payments.ReadablePaymentMethods;
import com.marketing.web.enums.PaymentOption;
import com.marketing.web.utils.RandomStringGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {


    @GetMapping("/methods")
    public ResponseEntity<ReadablePaymentMethods> getMethods(){
        ReadablePaymentMethods paymentMethods = new ReadablePaymentMethods();
        paymentMethods.setId(RandomStringGenerator.generateId());
        paymentMethods.setPaymentOptions(Arrays.asList(PaymentOption.values()));
        return ResponseEntity.ok(paymentMethods);
    }

}
