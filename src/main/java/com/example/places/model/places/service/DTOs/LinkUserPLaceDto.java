package com.example.places.model.places.service.DTOs;

import com.example.places.utils.enums.UserPlaceRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LinkUserPLaceDto {

    @NotBlank
    private String userId;

    @NotBlank
    @JsonProperty("place_nit")
    private String placeNIT;

    @NotNull
    private UserPlaceRole role;
}


