package com.example.places.exceptions;

import com.example.places.utils.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class DataNotFoundException extends ApiException {

    public DataNotFoundException(String message, ErrorCode errorCode){
        super(message, HttpStatus.NOT_FOUND, errorCode);
    }
}
