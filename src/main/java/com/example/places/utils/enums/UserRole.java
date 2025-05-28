package com.example.places.utils.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum UserRole {

    STANDARD_USER("standard_user"),
    AGGREGATOR("aggregator");

    UserRole(String value) {
        this.value = value;
    }

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    public static UserRole fromValue(String value) {
        return Arrays.stream(UserRole.values())
                .filter(role -> role.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró PlaceRole para el valor: " + value));
    }
}
