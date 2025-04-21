package com.example.places.model.places.service.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkUserPLaceDto {

    @NotBlank
    private String userId;

    @NotBlank
    @JsonProperty("place_nit")
    private String placeNIT;
}


