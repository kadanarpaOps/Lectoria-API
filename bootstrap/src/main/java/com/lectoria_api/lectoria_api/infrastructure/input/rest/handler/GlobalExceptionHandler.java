package com.lectoria_api.lectoria_api.infrastructure.input.rest.handler;

import com.lectoria_api.chapters.domain.exceptions.ChapterNotFoundException;
import com.lectoria_api.chapters.domain.exceptions.ChapterOwnershipException;
import com.lectoria_api.chapters.domain.exceptions.InvalidChapterDataException;
import com.lectoria_api.comments.domain.exceptions.CommentNotFoundException;
import com.lectoria_api.comments.domain.exceptions.CommentOwnershipException;
import com.lectoria_api.comments.domain.exceptions.InvalidCommentDataException;
import com.lectoria_api.auth.domain.exceptions.auth.InvalidRefreshAccessAttemptException;
import com.lectoria_api.auth.domain.exceptions.auth.NoAuthenticationFoundException;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorDetailsDTO;
import com.lectoria_api.common.infrastructure.input.rest.dto.ErrorResponseDTO;
import com.lectoria_api.auth.domain.exceptions.auth.IncorrectCredentialsException;
import com.lectoria_api.domain.exceptions.business.ExistingVerificationRequestIsPendingException;
import com.lectoria_api.domain.exceptions.business.NoAttemptsAvailableException;
import com.lectoria_api.domain.exceptions.business.VerificationCodeIsExpiredException;
import com.lectoria_api.domain.exceptions.business.VerificationInvalidCodeException;
import com.lectoria_api.domain.exceptions.business.VerificationIsExpiredException;
import com.lectoria_api.domain.exceptions.business.VerificationNotFoundException;
import com.lectoria_api.domain.exceptions.business.WrongVerificationCreationRequestScopeException;
import com.lectoria_api.notifications.domain.exceptions.business.FailedMailSenderOperation;
import com.lectoria_api.notifications.domain.exceptions.business.NotFoundNotifierException;
import com.lectoria_api.notifications.domain.exceptions.business.NotFoundTemplateException;
import com.lectoria_api.stories.domain.exceptions.GenreNotFoundException;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.domain.exceptions.StoryNotFoundException;
import com.lectoria_api.stories.domain.exceptions.StoryOwnershipException;
import com.lectoria_api.stories.domain.exceptions.StoryTitleAlreadyExistsException;
import com.lectoria_api.users.domain.exceptions.InvalidParametersForUserSearchException;
import com.lectoria_api.users.domain.exceptions.business.AdminCantBeDisabledException;
import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import com.lectoria_api.users.domain.exceptions.business.RoleWithNameNotExistsException;
import com.lectoria_api.users.domain.exceptions.business.SameFieldValueOnUpdateException;
import com.lectoria_api.users.domain.exceptions.business.UserAlreadyHaveThatRoleException;
import com.lectoria_api.users.domain.exceptions.business.UserNotHaveThatRoleException;
import com.lectoria_api.users.domain.exceptions.business.UserRegistrationConflictException;
import com.lectoria_api.users.domain.exceptions.business.UserRolesListCantBeEmptyException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldAlreadyExistsException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldNotExistsException;
import com.lectoria_api.users.domain.exceptions.keycloak.KeycloakRoleNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

    @ExceptionHandler(UserRegistrationConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleRegistrationConflict(UserRegistrationConflictException ex) {
        return errorResponse(ex, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserWithFieldAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserFieldConflict(UserWithFieldAlreadyExistsException ex) {
        return domainError(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            SameFieldValueOnUpdateException.class,
            UserAlreadyHaveThatRoleException.class,
            UserNotHaveThatRoleException.class,
            ExistingVerificationRequestIsPendingException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleConflict(RuntimeException ex) {
        return errorResponse(ex, HttpStatus.CONFLICT, domainMessage(ex, HttpStatus.CONFLICT));
    }

    @ExceptionHandler({
            RoleWithNameNotExistsException.class,
            KeycloakRoleNotFoundException.class,
            NotFoundNotifierException.class,
            NotFoundTemplateException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleMissingDomainResource(RuntimeException ex) {
        return errorResponse(ex, HttpStatus.NOT_FOUND, domainMessage(ex, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler({
            NoAuthenticationFoundException.class,
            InvalidRefreshAccessAttemptException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleInvalidAuthState(RuntimeException ex) {
        return errorResponse(ex, HttpStatus.UNAUTHORIZED, domainMessage(ex, HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler({
            VerificationInvalidCodeException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleVerificationFailure(RuntimeException ex) {
        return errorResponse(ex, HttpStatus.BAD_REQUEST, domainMessage(ex, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(VerificationNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleVerificationNotFound(VerificationNotFoundException ex) {
        return errorResponse(ex, HttpStatus.NOT_FOUND, domainMessage(ex, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler({VerificationIsExpiredException.class, VerificationCodeIsExpiredException.class})
    public ResponseEntity<ErrorResponseDTO> handleExpiredVerification(RuntimeException ex) {
        return errorResponse(ex, HttpStatus.GONE, domainMessage(ex, HttpStatus.GONE));
    }

    @ExceptionHandler(NoAttemptsAvailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleExhaustedVerificationAttempts(NoAttemptsAvailableException ex) {
        return errorResponse(ex, HttpStatus.TOO_MANY_REQUESTS, domainMessage(ex, HttpStatus.TOO_MANY_REQUESTS));
    }

    @ExceptionHandler(WrongVerificationCreationRequestScopeException.class)
    public ResponseEntity<ErrorResponseDTO> handleVerificationScope(
            WrongVerificationCreationRequestScopeException ex) {
        return errorResponse(ex, HttpStatus.FORBIDDEN, domainMessage(ex, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler({
            AdminCantBeDisabledException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleForbiddenOperation(RuntimeException ex) {
        return errorResponse(ex, HttpStatus.FORBIDDEN, domainMessage(ex, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(UserRolesListCantBeEmptyException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRoleList(UserRolesListCantBeEmptyException ex) {
        return errorResponse(ex, HttpStatus.BAD_REQUEST, domainMessage(ex, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(FailedOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleFailedOperation(FailedOperationException ex) {
        LOG.error("Falló una operación de aplicación: {}", ex.getOperation(), ex);
        HttpStatus status = failedOperationStatus(ex);
        return errorResponse(ex, status, failedOperationMessage(ex));
    }

    @ExceptionHandler(FailedMailSenderOperation.class)
    public ResponseEntity<ErrorResponseDTO> handleMailDeliveryFailure(FailedMailSenderOperation ex) {
        LOG.error("No se pudo enviar un correo de la aplicación", ex);
        return errorResponse(ex, HttpStatus.SERVICE_UNAVAILABLE,
                "No se pudo enviar el correo. Revisa los datos e inténtalo nuevamente más tarde.");
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
        return errorResponse(ex, HttpStatus.NOT_FOUND, "La ruta solicitada no existe.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = getValidationErrors(ex.getBindingResult());
        Map<String, String> errors = validationErrors.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> translateValidationMessage(entry.getKey(), entry.getValue()),
                        (first, ignored) -> first,
                        java.util.LinkedHashMap::new
                ));
        String firstField = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .findFirst()
                .orElse("solicitud");
        String firstMessage = errors.get(firstField);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Revisa el campo \"" + fieldLabel(firstField) + "\": " + firstMessage)
                        .details(doDetails(ex, errors))
                        .timestamp(now())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameter = fieldLabel(ex.getName());
        return errorResponse(ex, HttpStatus.BAD_REQUEST,
                "El dato \"" + parameter + "\" no tiene un formato válido.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadableRequest(HttpMessageNotReadableException ex) {
        return errorResponse(ex, HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud está vacío o tiene un formato inválido.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(MissingServletRequestParameterException ex) {
        return errorResponse(ex, HttpStatus.BAD_REQUEST,
                "Falta el dato obligatorio \"" + fieldLabel(ex.getParameterName()) + "\".");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> "El parámetro \"" + violation.getPropertyPath().toString()
                        + "\" no cumple con el formato requerido.")
                .orElse("Uno o más parámetros no son válidos.");
        return errorResponse(ex, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return errorResponse(ex, HttpStatus.METHOD_NOT_ALLOWED,
                "Esta operación no admite el método de solicitud utilizado.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return errorResponse(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El formato del contenido enviado no es compatible con esta operación.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataConflict(DataIntegrityViolationException ex) {
        LOG.warn("La base de datos rechazó datos por una restricción de integridad", ex);
        return errorResponse(ex, HttpStatus.CONFLICT,
                "No se pudo guardar la información porque entra en conflicto con un dato existente.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidArgument(IllegalArgumentException ex) {
        return errorResponse(ex, HttpStatus.BAD_REQUEST,
                "Uno de los datos enviados no es válido o está fuera del rango permitido.");
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
        if (ex instanceof UserWithFieldNotExistsException missing) {
            return "No se encontró ningún usuario con ese " + userFieldLabel(missing.getFieldName()) + ".";
        }
        if (ex instanceof UserWithFieldAlreadyExistsException existing) {
            return "Ya existe una cuenta con ese " + userFieldLabel(existing.getFieldName()) + ".";
        }
        if (ex instanceof StoryOwnershipException) return "No tienes permiso para modificar esta historia.";
        if (ex instanceof ChapterOwnershipException) return "No tienes permiso para modificar este capítulo.";
        if (ex instanceof CommentOwnershipException) return "No tienes permiso para modificar este comentario.";
        if (ex instanceof StoryTitleAlreadyExistsException) return "Ya existe una historia con ese título.";
        if (ex instanceof InvalidParametersForUserSearchException) {
            return "Indica un nombre de usuario o un correo electrónico, pero no ambos.";
        }
        if (ex instanceof SameFieldValueOnUpdateException) {
            return "El nuevo valor debe ser diferente al que ya tiene tu cuenta.";
        }
        if (ex instanceof UserAlreadyHaveThatRoleException) return "El usuario ya tiene asignado ese rol.";
        if (ex instanceof UserNotHaveThatRoleException) return "El usuario no tiene asignado ese rol.";
        if (ex instanceof RoleWithNameNotExistsException || ex instanceof KeycloakRoleNotFoundException) {
            return "El rol solicitado no existe o no está disponible.";
        }
        if (ex instanceof UserRolesListCantBeEmptyException) {
            return "El usuario debe conservar al menos un rol.";
        }
        if (ex instanceof AdminCantBeDisabledException) return "No se puede desactivar una cuenta administradora.";
        if (ex instanceof NoAuthenticationFoundException || ex instanceof InvalidRefreshAccessAttemptException) {
            return "La sesión no es válida o ha caducado. Inicia sesión nuevamente.";
        }
        if (ex instanceof VerificationInvalidCodeException) return "El código ingresado es incorrecto.";
        if (ex instanceof VerificationCodeIsExpiredException) return "El código venció. Solicita uno nuevo.";
        if (ex instanceof VerificationIsExpiredException) {
            return "El tiempo para completar la verificación venció. Inicia el proceso nuevamente.";
        }
        if (ex instanceof NoAttemptsAvailableException) {
            return "Se agotaron los intentos. Solicita un nuevo código para continuar.";
        }
        if (ex instanceof VerificationNotFoundException) {
            return "No hay una verificación vigente para completar esta operación.";
        }
        if (ex instanceof ExistingVerificationRequestIsPendingException) {
            return "Ya tienes una verificación pendiente para esta operación. Continúa ese proceso.";
        }
        if (ex instanceof WrongVerificationCreationRequestScopeException scope) {
            return "Este tipo de verificación debe solicitarse desde "
                    + ("Logged Out".equals(scope.getScope()) ? "la pantalla sin iniciar sesión."
                    : "una sesión iniciada.");
        }
        if (ex instanceof NotFoundTemplateException) return "No se encontró la plantilla de notificación solicitada.";
        if (ex instanceof NotFoundNotifierException) return "El canal de notificación solicitado no está disponible.";
        if (ex instanceof InvalidStoryDataException) return translateInvalidStoryMessage(ex.getMessage());
        if (ex instanceof InvalidChapterDataException) return translateInvalidChapterMessage(ex.getMessage());
        if (ex instanceof InvalidCommentDataException) return translateInvalidCommentMessage(ex.getMessage());
        return publicMessage(status);
    }

    private String failedOperationMessage(FailedOperationException ex) {
        String reason = ex.getReason();
        if ("At least one field is required".equals(reason)) {
            return "Indica al menos un dato para actualizar.";
        }
        if (reason != null && reason.startsWith("User has invalid role")) {
            return "No puedes asignar el rol de administrador desde el registro.";
        }
        if ("There was errors in the Token Validation".equals(reason)) {
            return "La sesión no es válida o ha caducado. Inicia sesión nuevamente.";
        }
        if (reason != null && reason.startsWith("User with value")) {
            return "No se encontró la cuenta en el servicio de autenticación. Inténtalo más tarde.";
        }
        if ("Errors in the Network Connection with Database".equals(reason)) {
            return switch (ex.getOperation()) {
                case "Creation" -> "No se pudo guardar la información. Inténtalo nuevamente más tarde.";
                case "Update" -> "No se pudieron guardar los cambios. Inténtalo nuevamente más tarde.";
                case "Deletion" -> "No se pudo eliminar la información. Inténtalo nuevamente más tarde.";
                default -> "No se pudo consultar la información. Inténtalo nuevamente más tarde.";
            };
        }
        if ("Errors in the Network Connection with Keycloak".equals(reason)) {
            return switch (ex.getOperation()) {
                case "Creation" -> "No se pudo crear la cuenta en este momento. Inténtalo nuevamente más tarde.";
                case "Authentication" -> "El servicio de inicio de sesión no está disponible. Inténtalo más tarde.";
                case "Update" -> "No se pudieron actualizar los datos de la cuenta. Inténtalo más tarde.";
                case "Deletion" -> "No se pudo eliminar la cuenta. Inténtalo más tarde.";
                default -> "No se pudo completar la operación de cuenta. Inténtalo más tarde.";
            };
        }
        return switch (ex.getOperation()) {
            case "Creation" -> "No se pudo completar la creación solicitada. Inténtalo nuevamente más tarde.";
            case "Update" -> "No se pudieron guardar los cambios. Inténtalo nuevamente más tarde.";
            case "Deletion" -> "No se pudo completar la eliminación. Inténtalo nuevamente más tarde.";
            case "Authentication" -> "No se pudo completar el inicio de sesión. Inténtalo más tarde.";
            default -> "No se pudo completar la operación solicitada. Inténtalo nuevamente más tarde.";
        };
    }

    private HttpStatus failedOperationStatus(FailedOperationException ex) {
        String reason = ex.getReason();
        if ("There was errors in the Token Validation".equals(reason)) return HttpStatus.UNAUTHORIZED;
        if ("At least one field is required".equals(reason)) return HttpStatus.BAD_REQUEST;
        if (reason != null && reason.startsWith("User has invalid role")) return HttpStatus.FORBIDDEN;
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    private String userFieldLabel(String field) {
        if (field == null) return "dato";
        return switch (field.toLowerCase()) {
            case "username", "nombre de usuario" -> "nombre de usuario";
            case "email", "useremail", "correo", "correo electrónico" -> "correo electrónico";
            case "id" -> "identificador de cuenta";
            default -> "dato indicado";
        };
    }

    private String fieldLabel(String field) {
        return switch (field) {
            case "username" -> "nombre de usuario";
            case "email", "userEmail" -> "correo electrónico";
            case "password", "newPassword" -> "contraseña";
            case "roleNames", "userRoles" -> "roles";
            case "idea" -> "idea para la sinopsis";
            case "tags" -> "etiquetas";
            case "content" -> "contenido";
            default -> field;
        };
    }

    private String translateValidationMessage(String field, String message) {
        String normalized = message == null ? "" : message.toLowerCase();
        if (normalized.contains("must not be null") || normalized.contains("must not be empty")
                || normalized.contains("must not be blank") || normalized.contains("must not be null")) {
            return switch (field) {
                case "username" -> "El nombre de usuario es obligatorio.";
                case "email", "userEmail" -> "El correo electrónico es obligatorio.";
                case "password", "newPassword" -> "La contraseña es obligatoria.";
                case "roleNames", "userRoles" -> "Selecciona al menos un rol.";
                case "idea" -> "Escribe una idea breve para generar la sinopsis.";
                case "content" -> "El contenido no puede estar vacío.";
                default -> "Este dato es obligatorio.";
            };
        }
        if (field.equals("username")) {
            return "Usa entre 4 y 30 caracteres: minúsculas, números, punto, guion o guion bajo.";
        }
        if (field.equals("email") || field.equals("userEmail")) {
            return "Ingresa un correo electrónico válido.";
        }
        if (field.equals("password") || field.equals("newPassword")) {
            return "Usa entre 7 y 30 caracteres e incluye una minúscula, una mayúscula, un número y un símbolo.";
        }
        if (field.equals("roleNames") || field.equals("userRoles")) return "Selecciona al menos un rol.";
        if (normalized.contains("size must be between") || normalized.contains("size must be less than")) {
            return switch (field) {
                case "idea" -> "La idea puede tener como máximo 2000 caracteres.";
                case "tags" -> "Puedes indicar hasta 10 etiquetas, de 40 caracteres como máximo cada una.";
                default -> "El valor supera la longitud permitida.";
            };
        }
        return "El valor ingresado no es válido.";
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
            case "Debes tomar el control de edición antes de guardar" ->
                    "Debes tomar el control de edición antes de guardar.";
            case "No se pudo generar la sinopsis. Inténtalo nuevamente." ->
                    "No se pudo generar la sinopsis. Inténtalo nuevamente.";
            case "La respuesta generada no es válida. Inténtalo nuevamente." ->
                    "El servicio generó una respuesta inválida. Inténtalo nuevamente.";
            case "Has alcanzado el límite de solicitudes. Inténtalo más tarde." ->
                    "Has alcanzado el límite de solicitudes. Inténtalo más tarde.";
            case "Puedes generar hasta tres sinopsis por minuto." ->
                    "Puedes generar hasta tres sinopsis por minuto.";
            case "La paginación no es válida" -> "La página o el tamaño solicitado no son válidos.";
            case "Solo un administrador puede iniciar una reactivación" ->
                    "Solo una persona administradora puede iniciar la reactivación.";
            case "No puedes responder esta solicitud" -> "No tienes permiso para responder esta solicitud.";
            case "No tienes permiso para editar este capítulo" ->
                    "No tienes permiso para editar este capítulo.";
            case "Este capítulo está siendo editado por otra persona" ->
                    "Otra persona está editando este capítulo. Inténtalo cuando termine.";
            case "No puedes invitarte a ti mismo" -> "No puedes enviarte una invitación a ti mismo.";
            case "Ya eres el autor de esta historia" -> "Ya eres el autor de esta historia.";
            case "Este usuario ya es coautor de la historia" -> "Este usuario ya es coautor de la historia.";
            case "Ya existe una solicitud pendiente para esta historia" ->
                    "Ya hay una solicitud pendiente para esta historia.";
            case "No se encontró la solicitud" -> "No se encontró la solicitud indicada.";
            case "La solicitud ya fue respondida" -> "Esta solicitud ya fue respondida.";
            case "Esta cuenta no puede solicitar reactivación" ->
                    "Esta cuenta no puede solicitar una reactivación.";
            case "Ya existe una solicitud de reactivación pendiente para esta cuenta" ->
                    "Ya hay una solicitud de reactivación pendiente para esta cuenta.";
            case "La solicitud de reactivación ya no está pendiente" ->
                    "La solicitud de reactivación ya fue atendida.";
            case "La cuenta ya está activa" -> "La cuenta ya está activa.";
            case "No se encontró el capítulo" -> "No se encontró el capítulo solicitado.";
            case "No se encontró la notificación" -> "No se encontró la notificación solicitada.";
            case "Indica un nombre de usuario o un correo, pero no ambos" ->
                    "Indica un nombre de usuario o un correo electrónico, pero no ambos.";
            default -> isKnownSpanishReason(reason.trim()) ? reason.trim() : null;
        };
    }

    private boolean isKnownSpanishReason(String reason) {
        return switch (reason) {
            case "Solo un administrador puede iniciar una reactivación",
                 "Indica un nombre de usuario o un correo, pero no ambos",
                 "La paginación no es válida",
                 "Debes tomar el control de edición antes de guardar" -> true;
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
