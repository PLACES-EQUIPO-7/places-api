package com.example.places.model.places.service.DTOs;

import com.example.places.utils.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusDTO {

    private Long shipmentId;

    private ShipmentStatus newStatus;

    private OffsetDateTime deliveredAt;
}
