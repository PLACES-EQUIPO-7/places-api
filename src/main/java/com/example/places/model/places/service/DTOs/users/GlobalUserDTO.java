package com.example.places.model.places.service.DTOs.users;

import com.example.places.utils.enums.DNIType;
import com.example.places.utils.enums.UserPlaceRole;
import com.example.places.utils.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalUserDTO {

    private String id;

    @JsonProperty("dni")
    @NotBlank(message = "dni is required")
    private String DNI;

    @JsonProperty("dni_type")
    @NotNull(message = "dni_type is required")
    private DNIType DNIType;

    @NotBlank(message = "first_name is required")
    private String firstName;

    @NotBlank(message = "last_name is required")
    private String lastName;

    @NotBlank(message = "user_name is required")
    private String userName;

    @NotBlank(message = "email is required")
    @Email
    private String email;

    @NotNull(message = "role is required")
    private UserRole role;

    private UserPlaceRole placeRole;

    private boolean isEnabled;

}
