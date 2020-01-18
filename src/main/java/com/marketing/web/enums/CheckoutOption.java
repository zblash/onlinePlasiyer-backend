package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CheckoutOption {
    ALL("ALL"),
    LIST("LIST");

    private String value;

    CheckoutOption(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CheckoutOption fromValue(String text) {
        for (CheckoutOption b : CheckoutOption.values()) {
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
