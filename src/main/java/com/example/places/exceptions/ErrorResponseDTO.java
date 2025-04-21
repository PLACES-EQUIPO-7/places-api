package com.example.places.exceptions;

import com.example.places.utils.enums.ErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private ErrorCode code;
    private String message;
}
