package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentOption {
    SCRD("SYSTEM_CREDIT"),
    MCRD("MERCHANT_CREDIT"),
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
