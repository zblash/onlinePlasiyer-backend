package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketStatus {
    OPEN("OPEN"),
    ANSWERED("ANSWERED"),
    CLOSED("CLOSED"),
    SOLVED("SOLVED");

    private String value;

    TicketStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TicketStatus fromValue(String text) {
        for (TicketStatus b : TicketStatus.values()) {
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

