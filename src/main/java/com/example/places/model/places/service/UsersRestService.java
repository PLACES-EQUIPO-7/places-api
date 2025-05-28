package com.example.places.model.places.service;

import com.example.places.exceptions.ApiException;
import com.example.places.exceptions.DataNotFoundException;
import com.example.places.model.places.service.DTOs.users.GlobalUserDTO;
import com.example.places.utils.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class UsersRestService {

    private final RestClient usersRestClient;

    private static final String USERS_GET_URL = "/api/users?user_id=%s";

    public GlobalUserDTO getUser(String userId) {

        GlobalUserDTO userCreated = null;

        try {
            userCreated = usersRestClient.get()
                    .uri(String.format(USERS_GET_URL, userId))
                    .retrieve()
                    .body(GlobalUserDTO.class);

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode().value() == 404) {
                throw new DataNotFoundException("User not found dni:" + userId, ErrorCode.USER_NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("Unexpected error occurred while getting user: {}", e.getMessage(), e);
            throw new ApiException("Unexpected error occurred while getting user", ErrorCode.INTERNAL_ERROR);
        }


        return userCreated;
    }

}
