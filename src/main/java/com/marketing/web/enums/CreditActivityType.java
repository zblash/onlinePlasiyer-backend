package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditActivityType {
    DEBT("DEBT"),
    CREDIT("CREDIT");

    private String value;

    CreditActivityType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CreditActivityType fromValue(String text) {
        for (CreditActivityType b : CreditActivityType.values()) {
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
