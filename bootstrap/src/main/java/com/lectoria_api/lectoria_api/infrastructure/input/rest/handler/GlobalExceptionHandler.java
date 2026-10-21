package com.lectoria_api.lectoria_api.infrastructure.input.rest.handler;

import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorDetailsDTO;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorResponseDTO;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponseDTO.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .details(doDetails(ex))
                        .timestamp(now())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = getValidationErrors(ex.getBindingResult());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid Input Parameters")
                        .details(doDetails(ex, errors))
                        .timestamp(now())
                        .build()
        );
    }

    private ErrorDetailsDTO doDetails(Exception ex, Map<String, String> errors) {
        ErrorDetailsDTO errorDetailsDTO = doDetails(ex);
        errorDetailsDTO.setErrorDetails(errors);
        return errorDetailsDTO;
    }

    private ErrorDetailsDTO doDetails(Exception ex) {
        return ErrorDetailsDTO.builder()
                .errorName(ex.getClass().getSimpleName())
                .build();
    }

    public Map<String, String> getValidationErrors(BindingResult errors) {
        return errors.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                DefaultMessageSourceResolvable::getDefaultMessage,
                                Collectors.joining(", ")
                        )
                ));
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("UTC-5"));
    }

}
