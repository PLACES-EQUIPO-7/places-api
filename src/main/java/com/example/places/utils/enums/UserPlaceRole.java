package com.example.places.utils.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserPlaceRole {

    PLACE_AGGREGATOR("PLACE_AGGREGATOR"),
    PLACE_OWNER("PLACE_OWNER"),
    PLACE_EMPLOYEE("PLACE_EMPLOYEE");

    private final String value;

    UserPlaceRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
