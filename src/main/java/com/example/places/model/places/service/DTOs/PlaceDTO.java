package com.example.places.model.places.service.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceDTO {

    @NotNull
    private Long id;

    @NotBlank
    private String nit;

    @NotNull
    private Double reputation;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String zipCode;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    private boolean isEnabled;

}
