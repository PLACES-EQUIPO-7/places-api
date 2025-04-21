package com.example.places.model.places.service.DTOs;

import com.example.places.utils.enums.UserPlaceRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddUserRelationshipDTO {

    @NotBlank
    private String userId;

    @NotBlank
    private Long placeId;

    @NotNull
    private UserPlaceRole role;
}
