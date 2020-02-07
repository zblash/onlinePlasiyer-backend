package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditType {
    SCRD("SYSTEM_CREDIT"),
    MCRD("MERCHANT_CREDIT");

    private String value;

    CreditType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CreditType fromValue(String text) {
        for (CreditType b : CreditType.values()) {
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
