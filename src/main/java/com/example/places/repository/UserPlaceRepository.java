package com.example.places.repository;

import com.example.places.model.places.Place;
import com.example.places.model.places.User;
import com.example.places.model.places.UserPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserPlaceRepository extends JpaRepository<UserPlace, Long> {


    @Query("select up from UserPlace up where up.user.id = :userId")
    List<UserPlace> getUserPlaceRelationshipsFromUser(String userId);

    @Query("select up from UserPlace up where up.place.id = :placeId")
    List<UserPlace> getUserPlaceRelationshipsFromPlace(Long placeId);

    boolean existsByUserAndPlace(User user, Place place);
}
