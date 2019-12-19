package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CartStatus {
    PND("PENDING"),
    PRCD("PROCEED"),
    NEW("NEW");

    private String value;

    CartStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CartStatus fromValue(String text) {
        for (CartStatus b : CartStatus.values()) {
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
