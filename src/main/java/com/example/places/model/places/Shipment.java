package com.example.places.model.places;

import com.example.places.utils.enums.ReceiverType;
import com.example.places.utils.enums.ShipmentStatus;
import com.example.places.utils.enums.ShipmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "shipment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Shipment {

    @Id
    @NotNull
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    private ShipmentType type;

    @Column(name = "status")
    private ShipmentStatus status;

    @Column(name = "receiver_id")
    private String receiverId;

    @Column(name = "receiver_type")
    private ReceiverType receiverType;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "phrase")
    private String phrase;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

}
