package com.example.places.model.places;

import com.example.places.utils.enums.UserPlaceRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "user_place",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"users_id", "place_id"})
        })
public class UserPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "role")
    private UserPlaceRole role;

    @Column(name = "is_enabled")
    private boolean isEnabled;

}
