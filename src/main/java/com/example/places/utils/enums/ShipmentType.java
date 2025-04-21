package com.example.places.utils.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ShipmentType {

    PICK_UP("PICK_UP"),
    DEVOLUTION("DEVOLUTION");

    private final String value;

    ShipmentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
