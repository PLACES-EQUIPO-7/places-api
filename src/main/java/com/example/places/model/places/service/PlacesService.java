package com.example.places.model.places.service;

import com.example.places.exceptions.DataNotFoundException;
import com.example.places.model.places.Place;
import com.example.places.model.places.User;
import com.example.places.model.places.UserPlace;
import com.example.places.model.places.service.DTOs.*;
import com.example.places.repository.PlaceRepository;
import com.example.places.repository.UserPlaceRepository;
import com.example.places.repository.UserRepository;
import com.example.places.utils.enums.ErrorCode;
import com.example.places.utils.enums.UserPlaceRole;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlacesService {

    private static final Logger log = LoggerFactory.getLogger(PlacesService.class);
    private final UserPlaceRepository userPlaceRepository;

    private final PlaceRepository placeRepository;

    private final UserRepository userRepository;

    public PlacesService(UserPlaceRepository userPlaceRepository, PlaceRepository placeRepository, UserRepository userRepository) {
        this.userPlaceRepository = userPlaceRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }
    
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


    public PlaceUsersDTO getPlaceUsers(String placeNit) {

        List<UserPlace> userPlaces  = userPlaceRepository.getUserPlaceRelationshipsFromPlace(placeNit);

        PlaceUsersDTO placeDTO = PlaceUsersDTO.builder()
                .place(buildPlaceDTO(userPlaces.get(0).getPlace()))
                .users(userPlaces.stream().map(up ->
                     PlaceUsersDTO.UserRelationship.builder()
                           .userId(up.getUser().getId())
                           .role(up.getRole())
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
}