package com.example.places.repository;

import com.example.places.model.places.User;
import com.example.places.model.places.UserPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

}
