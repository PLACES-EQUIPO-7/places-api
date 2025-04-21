package com.example.places.controller;

import com.example.places.exceptions.DataConflictException;
import com.example.places.exceptions.DataNotFoundException;
import com.example.places.exceptions.ErrorResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataNotFoundException(DataNotFoundException e) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setMessage(e.getMessage());
        errorResponseDTO.setCode(e.getErrorCode());

        return ResponseEntity.status(e.getHttpStatus()).body(errorResponseDTO);
    }

    @ExceptionHandler(value = DataConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataConflictException(DataConflictException e) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setMessage(e.getMessage());
        errorResponseDTO.setCode(e.getErrorCode());

        return ResponseEntity.status(e.getHttpStatus()).body(errorResponseDTO);
    }
}
