package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UnitType {
    KG("KG"),
    KL("KL"),
    AD("AD");

    private String value;

    UnitType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static UnitType fromValue(String text) {
        for (UnitType b : UnitType.values()) {
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
