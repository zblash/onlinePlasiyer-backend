package com.marketing.web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WsStatus {
    CREATED("CREATED"),
    UPDATED("UPDATED");

    private String value;

    WsStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static WsStatus fromValue(String text) {
        for (WsStatus b : WsStatus.values()) {
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

