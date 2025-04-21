package com.example.places.repository;

import com.example.places.model.places.Place;
import com.example.places.model.places.UserPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Place findByNit(String nitriteId);

}
