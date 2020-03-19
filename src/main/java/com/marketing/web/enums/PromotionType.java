package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PromotionType {
    PERCENT("PERCENT"),
    PROMOTION("PROMOTION");

    private String value;

    PromotionType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PromotionType fromValue(String text) {
        for (PromotionType b : PromotionType.values()) {
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
