package com.example.places.model.places;

import com.example.places.utils.enums.ReceiverType;
import com.example.places.utils.enums.ShipmentStatus;
import com.example.places.utils.enums.ShipmentType;
import jakarta.persistence.*;

@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipment_seq")
    @SequenceGenerator(
            name = "shipment_seq",
            sequenceName = "shipments_sequence",
            allocationSize = 1,
            initialValue = 400000000)
    @Column(name = "id")
    private Long id;

    @Column(name = "reputation")
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

}
