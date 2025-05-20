package com.example.places.model.places.service;

import com.example.places.exceptions.DataNotFoundException;
import com.example.places.model.places.Place;
import com.example.places.model.places.Shipment;
import com.example.places.model.places.User;
import com.example.places.model.places.UserPlace;
import com.example.places.model.places.service.DTOs.*;
import com.example.places.repository.PlaceRepository;
import com.example.places.repository.ShipmentRepository;
import com.example.places.repository.UserPlaceRepository;
import com.example.places.repository.UserRepository;
import com.example.places.utils.enums.ErrorCode;
import com.example.places.utils.enums.ShipmentStatus;
import com.example.places.utils.enums.ShipmentType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlacesService {

    private static final Logger log = LoggerFactory.getLogger(PlacesService.class);
    private final UserPlaceRepository userPlaceRepository;

    private final PlaceRepository placeRepository;

    private final UserRepository userRepository;

    private final ShipmentRepository shipmentRepository;

    
    public List<UserPLaceDTO> getPlacesByUser(@Valid @RequestParam String userId) {
        List<UserPlace> userPlace = userPlaceRepository.getUserPlaceRelationshipsFromUser(userId);

        return userPlace.stream()
                .map(up -> UserPLaceDTO.builder()
                        .place(buildPlaceDTO(up.getPlace()))
                        .user(UserDTO.builder()
                                .id(up.getUser().getId())
                                .build())
                        .role(up.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    public PlaceDTO getPlacesByDNI(@Valid String dni) {

        Place place = placeRepository.findByNit(dni);

        return buildPlaceDTO(place);

    }


    public void registerPlace(PlaceDTO placeDTO) {

        try {
            placeRepository.save(buildPlace(placeDTO));

        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("Place:%s already exist", placeDTO.getNit()));
        }

    }

    public void registerUser(UserDTO userDTO) {

        try {
            userRepository.save(buildUser(userDTO));

        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("User:%s already exist", userDTO.getId()));
        }

    }

    public void linkUserPace(LinkUserPLaceDto linkUserPLaceDto) {


        Place place = placeRepository.findByNit(linkUserPLaceDto.getPlaceNIT());

        if (place == null) {
            throw new DataNotFoundException("place not found", ErrorCode.PLACE_NOT_FOUND);
        }

        Optional<User> user = userRepository.findById(linkUserPLaceDto.getUserId());

        if (user.isEmpty()) {
            throw new DataNotFoundException("user not found", ErrorCode.USER_NOT_FOUND);
        }

        boolean alreadyLinked = userPlaceRepository.existsByUserAndPlace(user.get(), place);

        log.warn("not linked" + linkUserPLaceDto);
        if (!alreadyLinked) {
            log.warn("linked" + linkUserPLaceDto);
            userPlaceRepository.save(
                    UserPlace.builder()
                            .user(user.get())
                            .place(place)
                            .isEnabled(true)
                            .role(linkUserPLaceDto.getRole())
                            .build());
        }

    }

    public ShipmentDTO createShipment(ShipmentDTO shipmentDTO) {

        Shipment shipment = buildShipmentFromShipmentDTO(shipmentDTO);

        if (!placeRepository.existsById(shipmentDTO.getPlaceId())) {
            throw new DataNotFoundException("place not found", ErrorCode.PLACE_NOT_FOUND);
        }

        shipmentRepository.save(shipment);

        return shipmentDTO;
    }

    public List<ShipmentDTO> getShipments(ShipmentType type, ShipmentStatus status, Long placeId) {

        List<Shipment> shipments = shipmentRepository.findByPlaceAndTypeAndStatus(placeId, status, type);


        return shipments.stream().map(this::buildShipmentDTOFromShipment).collect(Collectors.toList());
    }

    public ShipmentDTO getShipmentById(Long id) {
        return shipmentRepository.findById(id).map(this::buildShipmentDTOFromShipment)
                .orElseThrow(() -> new DataNotFoundException("shipment not found", ErrorCode.SHIPMENT_NOT_FOUND));
    }

    public ShipmentDTO getShipmentByPhrase(String phrase) {
        return shipmentRepository.findByPhrase(phrase).map(this::buildShipmentDTOFromShipment)
                .orElseThrow(() -> new DataNotFoundException("shipment not found", ErrorCode.SHIPMENT_NOT_FOUND));
    }

    public BillingDTO billing(Integer month, Integer year, Long placeId) {

        OffsetDateTime from = OffsetDateTime.of(year, month, 2, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime to = OffsetDateTime.of(year, month + 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        List<Shipment> shipments = shipmentRepository.findByPlaceAndTypeAndStatusFilteringByDate(placeId, ShipmentStatus.DELIVERED, null, from, to);


        Double total = shipments.stream().map(e -> {
            if (e.getType() == ShipmentType.PICK_UP) {
                return 500.0;
            }
            if (e.getType() == ShipmentType.DEVOLUTION) {
                return 400.0;
            }
            return 0.0;
        }).reduce(0.0, Double::sum);

        return BillingDTO.builder()
                .deliveredShipments(shipments.stream().map(this::buildShipmentDTOFromShipment).collect(Collectors.toList()))
                .deliveryPrice(500.0)
                .devolutionPrice(400.0)
                .total(total)
                .build();

    }

    private Shipment buildShipmentFromShipmentDTO(ShipmentDTO shipmentDTO) {
        return Shipment.builder()
                .id(shipmentDTO.getId())
                .type(shipmentDTO.getType())
                .phrase(shipmentDTO.getPhrase())
                .receiverId(shipmentDTO.getReceiverId())
                .receiverType(shipmentDTO.getReceiverType())
                .status(shipmentDTO.getStatus())
                .place(Place.builder()
                        .id(shipmentDTO.getPlaceId())
                        .build())
                .deliveredAt(shipmentDTO.getDeliveredAt())
                .build();
    }


    private ShipmentDTO buildShipmentDTOFromShipment(Shipment shipment) {
        return ShipmentDTO.builder()
                .id(shipment.getId())
                .type(shipment.getType())
                .phrase(shipment.getPhrase())
                .receiverId(shipment.getReceiverId())
                .receiverType(shipment.getReceiverType())
                .status(shipment.getStatus())
                .placeId(shipment.getPlace().getId())
                .deliveredAt(shipment.getDeliveredAt())
                .build();
    }

    private User buildUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .build();

    }

    private Place buildPlace(PlaceDTO createPlaceDTO) {

        return Place.builder()
                .id(createPlaceDTO.getId())
                .name(createPlaceDTO.getName())
                .address(createPlaceDTO.getAddress())
                .reputation(5.0)
                .isEnabled(true)
                .zipCode(createPlaceDTO.getZipCode())
                .longitude(createPlaceDTO.getLongitude())
                .latitude(createPlaceDTO.getLatitude())
                .shipments(List.of())
                .nit(createPlaceDTO.getNit())
                .build();
    }


    public PlaceUsersDTO getPlaceUsers(Long placeId) {

        List<UserPlace> userPlaces  = userPlaceRepository.getUserPlaceRelationshipsFromPlace(placeId);

        PlaceUsersDTO placeDTO = PlaceUsersDTO.builder()
                .place(buildPlaceDTO(userPlaces.get(0).getPlace()))
                .users(userPlaces.stream().map(up ->
                     PlaceUsersDTO.UserRelationship.builder()
                           .userId(up.getUser().getId())
                           .role(up.getRole())
                             .isEnabled(up.getPlace().isEnabled())
                           .build()
                ).collect(Collectors.toList()))
                .build();

        return placeDTO;
    }

    private PlaceDTO buildPlaceDTO(Place place) {

        return PlaceDTO.builder()
                .nit(place.getNit())
                .address(place.getAddress())
                .name(place.getName())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .zipCode(place.getZipCode())
                .reputation(place.getReputation())
                .id(place.getId())
                .build();
    }

    public void changeStatus(@Valid ChangeStatusDTO changeStatusDTO) {

        Optional<Shipment> shipment = shipmentRepository.findById(changeStatusDTO.getShipmentId());

        if (shipment.isEmpty()) {
            throw new DataNotFoundException("shipment not found", ErrorCode.SHIPMENT_NOT_FOUND);
        }

        shipment.get().setStatus(changeStatusDTO.getNewStatus());

        if (changeStatusDTO.getNewStatus() == ShipmentStatus.DELIVERED) {
            shipment.get().setDeliveredAt(OffsetDateTime.now());
        }

        shipmentRepository.save(shipment.get());


    }


}