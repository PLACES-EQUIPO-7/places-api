package com.example.places.model.places.service.DTOs;

import com.example.places.utils.enums.ReceiverType;
import com.example.places.utils.enums.ShipmentStatus;
import com.example.places.utils.enums.ShipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentDTO {

    private Long id;

    @NotNull
    private ShipmentType type;

    @NotNull
    private ShipmentStatus status;

    @NotBlank
    private String receiverId;

    @NotNull
    private ReceiverType receiverType;

    @NotNull
    private Long placeId;

    @NotNull
    private String phrase;

    private OffsetDateTime deliveredAt;
}
