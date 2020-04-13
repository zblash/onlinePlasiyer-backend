package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityType {
    SYSTEM_CREDIT("SYSTEM_CREDIT"),
    MERCHANT_CREDIT("MERCHANT_CREDIT"),
    ORDER("ORDER"),
    ORDER_CANCEL("ORDER_CANCEL");

    private String value;

    ActivityType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ActivityType fromValue(String text) {
        for (ActivityType b : ActivityType.values()) {
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