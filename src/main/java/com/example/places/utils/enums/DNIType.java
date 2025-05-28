package com.example.places.utils.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DNIType {

    CC("cc"),
    CE("ce"),
    TI("ti");

    DNIType(String value) {
        this.value = value;
    }

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
