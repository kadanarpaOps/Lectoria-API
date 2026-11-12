package com.lectoria_api.lectoria_api.infrastructure.input.rest.handler;

import com.lectoria_api.chapters.domain.exceptions.ChapterNotFoundException;
import com.lectoria_api.chapters.domain.exceptions.ChapterOwnershipException;
import com.lectoria_api.chapters.domain.exceptions.InvalidChapterDataException;
import com.lectoria_api.comments.domain.exceptions.CommentNotFoundException;
import com.lectoria_api.comments.domain.exceptions.CommentOwnershipException;
import com.lectoria_api.comments.domain.exceptions.InvalidCommentDataException;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorDetailsDTO;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorResponseDTO;
import com.lectoria_api.auth.domain.exceptions.auth.IncorrectCredentialsException;
import com.lectoria_api.stories.domain.exceptions.GenreNotFoundException;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.domain.exceptions.StoryNotFoundException;
import com.lectoria_api.stories.domain.exceptions.StoryOwnershipException;
import com.lectoria_api.stories.domain.exceptions.StoryTitleAlreadyExistsException;
import com.lectoria_api.users.domain.exceptions.InvalidParametersForUserSearchException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        return errorResponse(ex, HttpStatus.UNAUTHORIZED, publicMessage(HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIncorrectCredentials(IncorrectCredentialsException ex) {
        return errorResponse(ex, HttpStatus.UNAUTHORIZED,
                "El usuario o correo y la contraseña no coinciden.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        return domainError(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        HttpStatus resolvedStatus = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        return errorResponse(ex, statusCode, publicMessage(resolvedStatus, ex.getReason()));
    }

    @ExceptionHandler({
            InvalidStoryDataException.class,
            InvalidChapterDataException.class,
            InvalidCommentDataException.class,
            InvalidParametersForUserSearchException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleInvalidDomainData(RuntimeException ex) {
        return domainError(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            StoryNotFoundException.class,
            GenreNotFoundException.class,
            ChapterNotFoundException.class,
            CommentNotFoundException.class,
            UserWithFieldNotExistsException.class
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
        LOG.error("Error interno al procesar la solicitud", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponseDTO.builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(publicMessage(HttpStatus.INTERNAL_SERVER_ERROR))
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
                        .message(publicMessage(HttpStatus.NOT_FOUND))
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
                        .message("Los datos enviados no son válidos. Revísalos e inténtalo nuevamente.")
                        .details(doDetails(ex, errors.keySet().stream()
                                .collect(Collectors.toMap(field -> field, field -> "El valor proporcionado no es válido."))))
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
                .errorName("Error de solicitud")
                .build();
    }

    private ResponseEntity<ErrorResponseDTO> domainError(Exception ex, HttpStatus status) {
        return ResponseEntity.status(status).body(ErrorResponseDTO.builder()
                .statusCode(status.value()).message(domainMessage(ex, status)).details(doDetails(ex)).timestamp(now()).build());
    }

    private String domainMessage(Exception ex, HttpStatus status) {
        if (ex instanceof StoryNotFoundException) return "No se encontró la historia solicitada.";
        if (ex instanceof GenreNotFoundException) return "No se encontró el género solicitado.";
        if (ex instanceof ChapterNotFoundException) return "No se encontró el capítulo solicitado.";
        if (ex instanceof CommentNotFoundException) return "No se encontró el comentario solicitado.";
        if (ex instanceof UserWithFieldNotExistsException) return "No se encontró un usuario con los datos indicados.";
        if (ex instanceof StoryOwnershipException) return "No tienes permiso para modificar esta historia.";
        if (ex instanceof ChapterOwnershipException) return "No tienes permiso para modificar este capítulo.";
        if (ex instanceof CommentOwnershipException) return "No tienes permiso para modificar este comentario.";
        if (ex instanceof StoryTitleAlreadyExistsException) return "Ya existe una historia con ese título.";
        if (ex instanceof InvalidParametersForUserSearchException) {
            return "Indica un nombre de usuario o un correo electrónico, pero no ambos.";
        }
        if (ex instanceof InvalidStoryDataException) return translateInvalidStoryMessage(ex.getMessage());
        if (ex instanceof InvalidChapterDataException) return translateInvalidChapterMessage(ex.getMessage());
        if (ex instanceof InvalidCommentDataException) return translateInvalidCommentMessage(ex.getMessage());
        return publicMessage(status);
    }

    private String translateInvalidStoryMessage(String message) {
        if (message == null) return "Los datos de la historia no son válidos. Revísalos e inténtalo nuevamente.";
        return switch (message) {
            case "Story data is required" -> "Debes completar los datos de la historia.";
            case "storyId is required" -> "Falta el identificador de la historia.";
            case "Invalid storyId" -> "El identificador de la historia no es válido.";
            case "Invalid genre or tag identifier" -> "El género o una etiqueta seleccionada no es válida.";
            case "Invalid tagNames JSON" -> "La lista de etiquetas enviada no tiene un formato válido.";
            case "Rating data is required" -> "Debes indicar la calificación de la historia.";
            case "Invalid storyId or score" -> "La historia o la calificación indicada no es válida.";
            case "Rating must be between 0 and 10" -> "La calificación debe estar entre 0 y 10.";
            case "A primary genre is required" -> "Debes seleccionar un género principal.";
            case "title is required" -> "El título de la historia es obligatorio.";
            case "Title must contain between 1 and 120 characters" ->
                    "El título debe tener entre 1 y 120 caracteres.";
            case "description is required", "description cannot be blank" ->
                    "La sinopsis de la historia es obligatoria.";
            case "Invalid story status" -> "El estado seleccionado para la historia no es válido.";
            case "Invalid pagination values" -> "Los valores de paginación no son válidos.";
            default -> "Los datos de la historia no son válidos. Revísalos e inténtalo nuevamente.";
        };
    }

    private String translateInvalidChapterMessage(String message) {
        if (message == null) return "Los datos del capítulo no son válidos. Revísalos e inténtalo nuevamente.";
        return switch (message) {
            case "storyId is required" -> "Falta el identificador de la historia.";
            case "Chapter data is required" -> "Debes completar los datos del capítulo.";
            case "Title cannot be blank" -> "El título del capítulo no puede estar vacío.";
            case "Chapter order must be positive", "Invalid chapter order" ->
                    "La posición del capítulo debe ser un número positivo.";
            case "Chapter order already exists" -> "Ya existe un capítulo en esa posición.";
            case "Chapter order is required" -> "Debes indicar la posición del capítulo.";
            case "Chapter order must be between 1 and the total number of chapters" ->
                    "La posición debe estar entre 1 y el total de capítulos de la historia.";
            case "Invalid storyId" -> "El identificador de la historia no es válido.";
            case "Invalid pagination values" -> "Los valores de paginación no son válidos.";
            default -> "Los datos del capítulo no son válidos. Revísalos e inténtalo nuevamente.";
        };
    }

    private String translateInvalidCommentMessage(String message) {
        if (message == null) return "Los datos del comentario no son válidos. Revísalos e inténtalo nuevamente.";
        return switch (message) {
            case "Comment data is required" -> "Debes completar los datos del comentario.";
            case "Invalid storyId or chapterId", "storyId or chapterId is required" ->
                    "Indica una historia o un capítulo válido para el comentario.";
            case "The chapter does not belong to the provided story" ->
                    "El capítulo seleccionado no pertenece a esa historia.";
            case "Provide exactly one of storyId or chapterId" ->
                    "El comentario debe pertenecer a una historia o a un capítulo, no a ambos.";
            case "content is required" -> "El comentario no puede estar vacío.";
            case "Invalid pagination values" -> "Los valores de paginación no son válidos.";
            default -> "Los datos del comentario no son válidos. Revísalos e inténtalo nuevamente.";
        };
    }

    private ResponseEntity<ErrorResponseDTO> errorResponse(Exception ex, HttpStatus status, String message) {
        return errorResponse(ex, (HttpStatusCode) status, message);
    }

    private ResponseEntity<ErrorResponseDTO> errorResponse(Exception ex, HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(ErrorResponseDTO.builder()
                .statusCode(status.value()).message(message).details(doDetails(ex)).timestamp(now()).build());
    }

    private String publicMessage(HttpStatus status) {
        return publicMessage(status, null);
    }

    private String publicMessage(HttpStatus status, String reason) {
        String translatedReason = translateReason(reason);
        if (translatedReason != null) {
            return translatedReason;
        }
        return switch (status) {
            case BAD_REQUEST -> "Los datos enviados no son válidos. Revísalos e inténtalo nuevamente.";
            case UNAUTHORIZED -> "Tu sesión no es válida o ha caducado. Inicia sesión nuevamente.";
            case FORBIDDEN -> "No tienes permiso para realizar esta acción.";
            case NOT_FOUND -> "No se encontró la información solicitada.";
            case CONFLICT -> "La solicitud entra en conflicto con la información existente.";
            case TOO_MANY_REQUESTS -> "Has realizado demasiados intentos. Espera un momento e inténtalo nuevamente.";
            case PAYLOAD_TOO_LARGE -> "El archivo supera el tamaño permitido.";
            case BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT ->
                    "No se pudo completar la operación por un problema con un servicio externo. Inténtalo más tarde.";
            default -> "Ocurrió un error al procesar la solicitud. Inténtalo más tarde.";
        };
    }

    private String translateReason(String reason) {
        if (reason == null) {
            return null;
        }
        return switch (reason.trim()) {
            case "Image upload failed", "Image removal failed" ->
                    "No se pudo actualizar la imagen. Inténtalo nuevamente más tarde.";
            case "Image is required" -> "Selecciona una imagen para continuar.";
            case "Image exceeds the 10 MB limit" -> "La imagen supera el tamaño máximo de 10 MB.";
            case "Only image uploads are supported" -> "El archivo seleccionado no es una imagen válida.";
            case "Image storage is not configured" ->
                    "El servicio de imágenes no está disponible. Inténtalo más tarde.";
            case "Invalid image folder" -> "La ubicación solicitada para la imagen no es válida.";
            case "The destination email is already in use" ->
                    "Ese correo electrónico ya está registrado por otra cuenta.";
            case "Wait until the current code expires before requesting another" ->
                    "Espera a que venza el código actual antes de solicitar otro.";
            case "A registration code can only be resent once" ->
                    "El código de registro solo se puede reenviar una vez.";
            case "Create a new email verification for the new destination" ->
                    "Solicita una nueva verificación para el correo electrónico que deseas usar.";
            case "This verification type requires an authenticated user" ->
                    "Inicia sesión para solicitar este tipo de verificación.";
            case "This verification type must be requested from the public scope" ->
                    "Solicita este tipo de verificación desde la pantalla pública correspondiente.";
            case "No unverified registration was found" ->
                    "No se encontró un registro pendiente para los datos indicados.";
            case "No active verification was found" ->
                    "No se encontró una verificación activa para los datos indicados.";
            default -> isKnownSpanishReason(reason.trim()) ? reason.trim() : null;
        };
    }

    private boolean isKnownSpanishReason(String reason) {
        return switch (reason) {
            case "Solo un administrador puede iniciar una reactivación",
                 "Indica un nombre de usuario o un correo, pero no ambos",
                 "No puedes responder esta solicitud",
                 "No tienes permiso para editar este capítulo",
                 "Este capítulo está siendo editado por otra persona",
                 "No puedes invitarte a ti mismo",
                 "Ya eres el autor de esta historia",
                 "Este usuario ya es coautor de la historia",
                 "Ya existe una solicitud pendiente para esta historia",
                 "No se encontró la solicitud",
                 "La solicitud ya fue respondida",
                 "Esta cuenta no puede solicitar reactivación",
                 "Ya existe una solicitud de reactivación pendiente para esta cuenta",
                 "La solicitud de reactivación ya no está pendiente",
                 "La cuenta ya está activa",
                 "No se encontró el capítulo",
                 "La paginación no es válida",
                 "No se encontró la notificación",
                 "No se pudo generar la sinopsis. Inténtalo nuevamente.",
                 "La respuesta generada no es válida. Inténtalo nuevamente.",
                 "Has alcanzado el límite de solicitudes. Inténtalo más tarde.",
                 "Puedes generar hasta tres sinopsis por minuto." -> true;
            default -> false;
        };
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
