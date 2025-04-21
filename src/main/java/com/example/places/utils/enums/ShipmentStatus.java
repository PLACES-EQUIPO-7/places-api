package com.example.places.utils.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ShipmentStatus {

    RECEIVED("RECEIVED"),
    PENDING("PENDING"),
    DELIVERED("DELIVERED"),
    LOST("LOST"),
    EXPIRED("EXPIRED");

    private final String value;

    ShipmentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
