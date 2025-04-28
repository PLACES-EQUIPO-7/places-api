package com.example.places.controller;

import com.example.places.model.places.service.DTOs.*;
import com.example.places.model.places.service.PlacesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlacesController {

    private final PlacesService placesService;

    public PlacesController(PlacesService placesService) {
        this.placesService = placesService;
    }

    @GetMapping("/by/user/{id}")
    public ResponseEntity<List<UserPLaceDTO>> getMyPlaces(@PathVariable @Valid String id) {

        List<UserPLaceDTO> placeUsersDTOS = placesService.getPlacesByUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(placeUsersDTOS);

    }

    @GetMapping("/by/nit/{dni}")
    public ResponseEntity<PlaceDTO> getPLaceByNit(@PathVariable @Valid String nit) {

        PlaceDTO placeUsersDTOS = placesService.getPlacesByDNI(nit);

        return ResponseEntity.status(HttpStatus.OK).body(placeUsersDTOS);

    }

    @PostMapping("/create")
    public ResponseEntity<Void> createPlace(@Valid @RequestBody PlaceDTO placeDTO) {

        placesService.registerPlace(placeDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register/user")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserDTO userDTO) {

        placesService.registerUser(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/link/user_place")
    public ResponseEntity<Void> linkUserPlace(@Valid @RequestBody LinkUserPLaceDto linkUserPLaceDto) {

        placesService.linkUserPace(linkUserPLaceDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/place/users")
    public ResponseEntity<PlaceUsersDTO> linkUserPlace(@Valid @RequestParam String placeNit) {

        PlaceUsersDTO placeUsersDTO = placesService.getPlaceUsers(placeNit);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



}
