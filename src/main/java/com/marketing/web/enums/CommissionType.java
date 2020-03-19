package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommissionType {
    ALL("ALL"),
    PRODUCT("PRODUCT"),
    USER("USER");

    private String value;

    CommissionType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CommissionType fromValue(String text) {
        for (CommissionType b : CommissionType.values()) {
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
