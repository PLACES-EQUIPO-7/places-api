package com.example.places.model.places.service;

import com.example.places.exceptions.DataConflictException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlacesService {

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

    @Transactional
    public void registerPlaceAndPlaceOwner(UserPLaceDTO userPLaceDTO) {

        User user = userRepository.save(buildUser(userPLaceDTO.getUser()));

        Place place = null;
        try {
            place = placeRepository.save(buildPlace(userPLaceDTO.getPlace()));

        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("place already created", ErrorCode.PLACE_ALREADY_EXISTS);
        }

        UserPlace userPlace = UserPlace.builder()
                .user(user)
                .place(place)
                .role(UserPlaceRole.PLACE_OWNER)
                .isEnabled(true)
                .build();

        userPlaceRepository.save(userPlace);

    }

    @Transactional
    public void linkUserPace(LinkUserPLaceDto linkUserPLaceDto) {


        Place place = placeRepository.findByNit(linkUserPLaceDto.getPlaceNIT());

        if (place == null) {
            throw new DataNotFoundException("place not found", ErrorCode.PLACE_NOT_FOUND);
        }

        User user = userRepository.save(User.builder().id(linkUserPLaceDto.getUserId()).build());

        boolean alreadyLinked = userPlaceRepository.existsByUserAndPlace(user, place);

        if (!alreadyLinked) {
            userPlaceRepository.save(
                    UserPlace.builder()
                            .user(user)
                            .place(place)
                            .isEnabled(true)
                            .role(UserPlaceRole.PLACE_EMPLOYEE)
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