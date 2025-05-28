package com.example.places.model.places.service;

import com.example.places.exceptions.DataNotFoundException;
import com.example.places.model.places.Place;
import com.example.places.model.places.Shipment;
import com.example.places.model.places.User;
import com.example.places.model.places.UserPlace;
import com.example.places.model.places.service.DTOs.*;
import com.example.places.model.places.service.DTOs.users.GlobalUserDTO;
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

    private final EmailService emailService;

    private final UsersRestService usersRestService;

    
    public List<UserPLaceDTO> getPlacesByUser(@Valid @RequestParam String userId) {
        List<UserPlace> userPlace = userPlaceRepository.getUserPlaceRelationshipsFromUser(userId);

        return userPlace.stream()
                .map(up -> UserPLaceDTO.builder()
                        .place(buildPlaceDTO(up.getPlace()))
                        .user(UserDTO.builder()
                                .id(up.getUser().getId())
                                .build())
                        .role(up.getRole())
                        .isEnabled(up.isEnabled())
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

        if (!placeRepository.existsById(shipmentDTO.getPlaceId())) {
            throw new DataNotFoundException("place not found", ErrorCode.PLACE_NOT_FOUND);
        }

        Shipment shipment = shipmentRepository.save(buildShipmentFromShipmentDTO(shipmentDTO));


        if (ShipmentStatus.PENDING.equals(shipment.getStatus())) {

            try {
                GlobalUserDTO userDTO = usersRestService.getUser(shipment.getReceiverId());
                Place place = placeRepository.findById(shipment.getPlace().getId())
                        .orElseThrow(() -> new DataNotFoundException("place not found", ErrorCode.PLACE_NOT_FOUND));
                String subject = buildSubject(shipmentDTO, userDTO);
                String body = buildBody(shipmentDTO, userDTO, place);
                emailService.sendEmail(userDTO.getEmail(), subject, body);
            } catch (Exception e) {
                log.error("Error sending email: {}", e.getMessage());
            }

        }


        return shipmentDTO;
    }

    private String buildBody(ShipmentDTO shipmentDTO, GlobalUserDTO userDTO, Place place) {

        if (shipmentDTO.getType() == ShipmentType.PICK_UP
                && shipmentDTO.getStatus() == ShipmentStatus.PENDING) {
            return String.format("""
                    <!DOCTYPE html>
                    <html lang="es">
                    <head>
                      <meta charset="UTF-8">
                      <title>Envío en camino</title>
                      <style>
                        body {
                          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                          background-color: #f4f4f4;
                          color: #333;
                          padding: 20px;
                        }
                        .container {
                          background-color: white;
                          border-radius: 8px;
                          padding: 25px;
                          max-width: 600px;
                          margin: 0 auto;
                          box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                        }
                        h2 {
                          color: #2c3e50;
                        }
                        .info {
                          margin: 20px 0;
                          font-size: 16px;
                          line-height: 1.6;
                        }
                        .keyword {
                          font-weight: bold;
                          color: #d35400;
                          font-size: 18px;
                        }
                        .footer {
                          margin-top: 30px;
                          font-size: 13px;
                          color: #888;
                          text-align: center;
                        }
                      </style>
                    </head>
                    <body>
                      <div class="container">
                        <h2>¡Tu envío ya está en camino! 🚚</h2>
                        <div class="info">
                          Tu envío <strong>%s</strong> ya se encuentra en camino.<br>
                          Llegará a la agencia <strong>%s</strong> en la dirección:<br>
                          <em>%s</em><br><br>
                          Para reclamarlo, usa la palabra clave:<br>
                          <span class="keyword">%s</span>
                        </div>
                        <div class="footer">
                          Gracias por confiar en nosotros.
                        </div>
                      </div>
                    </body>
                    </html>
                    """, shipmentDTO.getId(), place.getName(), place.getAddress(),  shipmentDTO.getPhrase());
        }

        if (shipmentDTO.getType() == ShipmentType.DEVOLUTION
                && shipmentDTO.getStatus() == ShipmentStatus.PENDING) {
            return String.format("""
                    <!DOCTYPE html>
                                           <html lang="es">
                                           <head>
                                             <meta charset="UTF-8">
                                             <title>Instrucciones de devolución</title>
                                             <style>
                                               body {
                                                 font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                                 background-color: #f0f2f5;
                                                 color: #333;
                                                 padding: 20px;
                                               }
                                               .container {
                                                 background-color: #ffffff;
                                                 border-radius: 8px;
                                                 padding: 25px;
                                                 max-width: 600px;
                                                 margin: 0 auto;
                                                 box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                                               }
                                               h2 {
                                                 color: #c0392b;
                                               }
                                               .info {
                                                 margin: 20px 0;
                                                 font-size: 16px;
                                                 line-height: 1.6;
                                               }
                                               .highlight {
                                                 font-weight: bold;
                                                 color: #2980b9;
                                               }
                                               .code {
                                                 display: inline-block;
                                                 background-color: #f6f6f6;
                                                 padding: 6px 12px;
                                                 border-radius: 5px;
                                                 font-family: monospace;
                                                 color: #e74c3c;
                                                 font-size: 16px;
                                               }
                                               .footer {
                                                 margin-top: 30px;
                                                 font-size: 13px;
                                                 color: #888;
                                                 text-align: center;
                                               }
                                             </style>
                                           </head>
                                           <body>
                                             <div class="container">
                                               <h2>Instrucciones para devolver tu paquete 📦</h2>
                                               <div class="info">
                                                 Lleva el paquete <span class="highlight">%s</span> a la agencia <span class="highlight">%s</span> ubicada en:<br>
                                                 <em>%s</em><br><br>
                                                 Presenta la siguiente clave de devolución:<br>
                                                 <span class="code">%s</span>
                                               </div>
                                               <div class="footer">
                                                 Si tienes dudas, contáctanos. Estamos aquí para ayudarte.
                                               </div>
                                             </div>
                                           </body>
                                           </html>
                    
            """, shipmentDTO.getId(), place.getName(), place.getAddress(), shipmentDTO.getPhrase());
        }

        return null;
    }

    private String buildSubject(ShipmentDTO shipmentDTO, GlobalUserDTO userDTO) {
       
        if (shipmentDTO.getType() == ShipmentType.PICK_UP 
                && shipmentDTO.getStatus() == ShipmentStatus.PENDING) {
            return String.format("Hola %s,tu envío esta en camino", userDTO.getFirstName());
        }

        if (shipmentDTO.getType() == ShipmentType.DEVOLUTION
                && shipmentDTO.getStatus() == ShipmentStatus.PENDING) {
            return String.format("Hola %s, estas son las instrucciones de devolucion", userDTO.getFirstName());
        }
        
        return null;
        
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
                .isEnabled(place.isEnabled())
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

        if (ShipmentType.PICK_UP.equals(shipment.get().getType())
            && ShipmentStatus.RECEIVED.equals(changeStatusDTO.getNewStatus())) {

            try {
                GlobalUserDTO userDTO = usersRestService.getUser(shipment.get().getReceiverId());
                Place place = placeRepository.findById(shipment.get().getPlace().getId())
                        .orElseThrow(() -> new DataNotFoundException("place not found", ErrorCode.PLACE_NOT_FOUND));
                String body = String.format("""
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                          <meta charset="UTF-8">
                          <title>Envío recibido en agencia</title>
                          <style>
                            body {
                              font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                              background-color: #eef2f5;
                              color: #333;
                              padding: 20px;
                            }
                            .container {
                              background-color: #ffffff;
                              border-radius: 10px;
                              padding: 25px;
                              max-width: 600px;
                              margin: auto;
                              box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
                            }
                            h2 {
                              color: #27ae60;
                            }
                            .info {
                              margin: 20px 0;
                              font-size: 16px;
                              line-height: 1.6;
                            }
                            .highlight {
                              font-weight: bold;
                              color: #2c3e50;
                            }
                            .code {
                              display: inline-block;
                              background-color: #f4f4f4;
                              padding: 8px 14px;
                              border-radius: 6px;
                              font-family: monospace;
                              color: #d35400;
                              font-size: 16px;
                              margin-top: 5px;
                            }
                            .footer {
                              margin-top: 30px;
                              font-size: 13px;
                              color: #777;
                              text-align: center;
                            }
                          </style>
                        </head>
                        <body>
                          <div class="container">
                            <h2>¡Tu envío ya está en la agencia! 📬</h2>
                            <div class="info">
                              Tu envío <span class="highlight">%s</span> ha sido recibido en la agencia <span class="highlight">%s</span> ubicada en:<br>
                              <em>%s</em><br><br>
                              Recuerda que puedes reclamarlo con la siguiente clave:<br>
                              <span class="code">%s</span>
                            </div>
                            <div class="footer">
                              Gracias por usar nuestro servicio. ¡Estamos para ayudarte!
                            </div>
                          </div>
                        </body>
                        </html>
                        
                        """, shipment.get().getId(), place.getName(), place.getAddress(), shipment.get().getPhrase());
                String subject = String.format(
                        "Hola %s, ya puedes ir a retirar tu envio %s", userDTO.getFirstName(), shipment.get().getId());
                emailService.sendEmail(userDTO.getEmail(), subject, body);
            } catch (Exception e) {
                log.error("Error sending email: {}", e.getMessage());
            }

        }

        shipmentRepository.save(shipment.get());


    }


}