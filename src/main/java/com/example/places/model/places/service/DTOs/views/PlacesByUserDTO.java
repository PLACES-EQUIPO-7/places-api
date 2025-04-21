package com.example.places.model.places.service.DTOs.views;

import com.example.places.model.places.service.DTOs.PlaceDTO;
import com.example.places.model.places.service.DTOs.UserDTO;
import jdk.dynalink.linker.LinkerServices;

import java.util.List;

public class PlacesByUserDTO {

    private UserDTO user;

    private List<PlaceDTO> places;
}
