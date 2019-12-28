package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ImportanceLevel {
    LOW("LOW"),
    MID("MIDDLE"),
    URG("URGENT");

    private String value;

    ImportanceLevel(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ImportanceLevel fromValue(String text) {
        for (ImportanceLevel b : ImportanceLevel.values()) {
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
