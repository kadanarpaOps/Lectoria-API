package com.lectoria_api.lectoria_api.infrastructure.input.rest.handler;

import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorDetailsDTO;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponseDTO.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .message(ex.getMessage())
                        .details(doDetails(ex))
                        .timestamp(now())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponseDTO.builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(ex.getMessage())
                        .details(doDetails(ex))
                        .timestamp(now())
                        .build()
        );
    }

    private ErrorDetailsDTO doDetails(Exception ex) {
        return ErrorDetailsDTO.builder()
                .errorName(ex.getClass().getSimpleName())
                .build();
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("UTC-5"));
    }

}
