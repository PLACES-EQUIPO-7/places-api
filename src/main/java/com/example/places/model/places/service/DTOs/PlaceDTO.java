package com.example.places.model.places.service.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceDTO {

    private Long id;

    private String nit;

    private Double reputation;

    private String name;

    private String address;

    private String zipCode;

    private Double longitude;

    private Double latitude;

}
