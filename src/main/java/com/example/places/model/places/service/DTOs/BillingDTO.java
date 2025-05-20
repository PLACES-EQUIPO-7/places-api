package com.example.places.model.places.service.DTOs;

import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDTO {

    private List<ShipmentDTO> deliveredShipments;

    private Double devolutionPrice;

    private Double deliveryPrice;

    private Double total;


}
