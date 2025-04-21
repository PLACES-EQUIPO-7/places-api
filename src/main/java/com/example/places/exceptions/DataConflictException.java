package com.example.places.exceptions;

import com.example.places.utils.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class DataConflictException extends ApiException {

    public DataConflictException(String message, ErrorCode errorCode){
        super(message, HttpStatus.CONFLICT, errorCode);
    }
}
