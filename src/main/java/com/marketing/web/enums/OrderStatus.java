package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    FINISHED("FINISHED"),
    PREPARED("PREPARED"),
    NEW("NEW"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED"),
    CANCEL_REQUEST("CANCEL_REQUEST");

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static OrderStatus fromValue(String text) {
        for (OrderStatus b : OrderStatus.values()) {
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
