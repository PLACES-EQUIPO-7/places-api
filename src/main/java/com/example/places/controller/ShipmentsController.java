package com.example.places.controller;

import com.example.places.model.places.service.DTOs.*;
import com.example.places.model.places.service.EmailService;
import com.example.places.model.places.service.PlacesService;
import com.example.places.utils.enums.ShipmentStatus;
import com.example.places.utils.enums.ShipmentType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@Slf4j
public class ShipmentsController {

    private final PlacesService placesService;

    public ShipmentsController(PlacesService placesService) {
        this.placesService = placesService;
    }


    @PostMapping("/create/shipment")
    public ResponseEntity<ShipmentDTO> createPlace(@Valid @RequestBody ShipmentDTO shipmentDTO) {

        ShipmentDTO result = placesService.createShipment(shipmentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/shipments")
    public ResponseEntity<List<ShipmentDTO>> getShipments(
            @RequestParam(value = "place_id") Long placeId,
            @RequestParam(value = "type", required = false) ShipmentType type,
            @RequestParam(value = "status", required = false) ShipmentStatus status) {

        List<ShipmentDTO> result = placesService.getShipments(type, status, placeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/shipment/by/id/{id}")
    public ResponseEntity<ShipmentDTO> getShipmentBYId(@PathVariable Long id) {

        ShipmentDTO result = placesService.getShipmentById(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/shipment/by/phrase/{phrase}")
    public ResponseEntity<ShipmentDTO> getShipmentBYId(@PathVariable String phrase) {

        ShipmentDTO result = placesService.getShipmentByPhrase(phrase);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/billing")
    public ResponseEntity<BillingDTO> facturacion(
            @RequestParam(value = "place_id") Long placeId,
            @RequestParam(value = "month") Integer month,
            @RequestParam(value = "year") Integer year) {

        BillingDTO result = placesService.billing(month, year, placeId);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/change/status")
    public ResponseEntity<Void> changeStatus(@Valid @RequestBody ChangeStatusDTO changeStatusDTO) {
        placesService.changeStatus(changeStatusDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }


}
