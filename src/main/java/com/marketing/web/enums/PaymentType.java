package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    CREDIT_CARD("CREDIT_CARD"),
    CASH("CASH"),
    RUNNING_ACCOUNT("RUNNING_ACCOUNT");

    private String value;

    PaymentType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PaymentType fromValue(String text) {
        for (PaymentType b : PaymentType.values()) {
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
