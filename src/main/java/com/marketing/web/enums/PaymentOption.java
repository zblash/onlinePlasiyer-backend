package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentOption {
    SYSTEM_CREDIT("SYSTEM_CREDIT"),
    MERCHANT_CREDIT("MERCHANT_CREDIT"),
    CCOD("CCOD"),
    COD("COD");

    private String value;

    PaymentOption(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PaymentOption fromValue(String text) {
        for (PaymentOption b : PaymentOption.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}
