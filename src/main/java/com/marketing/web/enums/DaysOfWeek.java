package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DaysOfWeek {
    MONDAY("MONDAY"),
    TUESDAY("TUESDAY"),
    WEDNESDAY("WEDNESDAY"),
    THURSDAY("THURSDAY"),
    FRIDAY("FRIDAY"),
    SATURDAY("SATURDAY"),
    SUNDAY("SUNDAY");

    private String value;

    DaysOfWeek(String value) {
        this.value = value;
    }

    @JsonCreator
    public static DaysOfWeek fromValue(String text) {
        for (DaysOfWeek b : DaysOfWeek.values()) {
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
