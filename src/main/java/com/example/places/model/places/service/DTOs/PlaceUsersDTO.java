package com.example.places.model.places.service.DTOs;

import com.example.places.utils.enums.UserPlaceRole;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceUsersDTO {
    
    private List<UserRelationship> users;
    
    private PlaceDTO place;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRelationship {

        private String userId;

        private UserPlaceRole role;

    }
}



