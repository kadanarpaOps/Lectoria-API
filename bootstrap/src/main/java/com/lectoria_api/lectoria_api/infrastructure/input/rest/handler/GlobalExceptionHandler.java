package com.lectoria_api.lectoria_api.infrastructure.input.rest.handler;

import com.lectoria_api.chapters.domain.exceptions.ChapterNotFoundException;
import com.lectoria_api.chapters.domain.exceptions.ChapterOwnershipException;
import com.lectoria_api.chapters.domain.exceptions.InvalidChapterDataException;
import com.lectoria_api.comments.domain.exceptions.CommentNotFoundException;
import com.lectoria_api.comments.domain.exceptions.CommentOwnershipException;
import com.lectoria_api.comments.domain.exceptions.InvalidCommentDataException;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorDetailsDTO;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorResponseDTO;
import com.lectoria_api.stories.domain.exceptions.GenreNotFoundException;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.domain.exceptions.StoryNotFoundException;
import com.lectoria_api.stories.domain.exceptions.StoryOwnershipException;
import com.lectoria_api.stories.domain.exceptions.StoryTitleAlreadyExistsException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;
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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(
                ErrorResponseDTO.builder()
                        .statusCode(status.value())
                        .message(ex.getReason() == null ? status.getReasonPhrase() : ex.getReason())
                        .details(doDetails(ex))
                        .timestamp(now())
                        .build()
        );
    }

    @ExceptionHandler({
            InvalidStoryDataException.class,
            InvalidChapterDataException.class,
            InvalidCommentDataException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleInvalidDomainData(RuntimeException ex) {
        return domainError(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            StoryNotFoundException.class,
            GenreNotFoundException.class,
            ChapterNotFoundException.class,
            CommentNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleDomainNotFound(RuntimeException ex) {
        return domainError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            StoryOwnershipException.class,
            ChapterOwnershipException.class,
            CommentOwnershipException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleDomainOwnership(RuntimeException ex) {
        return domainError(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(StoryTitleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleStoryConflict(StoryTitleAlreadyExistsException ex) {
        return domainError(ex, HttpStatus.CONFLICT);
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

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponseDTO> handleMalformedRequest(Exception ex) {
        return domainError(ex, HttpStatus.BAD_REQUEST);
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

    private ResponseEntity<ErrorResponseDTO> domainError(Exception ex, HttpStatus status) {
        return ResponseEntity.status(status).body(ErrorResponseDTO.builder()
                .statusCode(status.value()).message(ex.getMessage()).details(doDetails(ex)).timestamp(now()).build());
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
