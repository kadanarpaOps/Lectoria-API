package com.lectoria_api.lectoria_api.infrastructure.input.rest.handler;

import com.lectoria_api.auth.domain.exceptions.auth.IncorrectCredentialsException;
import com.lectoria_api.users.domain.exceptions.business.FailedOperationException;
import com.lectoria_api.users.domain.exceptions.business.UserRegistrationConflictException;
import com.lectoria_api.users.domain.exceptions.business.UserWithFieldAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.server.ResponseStatusException;

import static com.lectoria_api.users.domain.constants.Constants.EMAIL;
import static com.lectoria_api.users.domain.constants.Constants.CREATION_OP;
import static com.lectoria_api.users.domain.constants.Constants.DATABASE_ERR_CONNECTION;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void incorrectCredentialsHaveTheirOwnMessage() {
        var response = handler.handleIncorrectCredentials(new IncorrectCredentialsException());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("El usuario o correo y la contraseña no coinciden.", response.getBody().getMessage());
    }

    @Test
    void registrationConflictExplainsWhichDataMayBeInUse() {
        var response = handler.handleRegistrationConflict(new UserRegistrationConflictException());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El nombre de usuario o el correo electrónico ya está asociado a una cuenta.",
                response.getBody().getMessage());
    }

    @Test
    void duplicateEmailIsResolvedToAConflictWithAUserFacingMessage() {
        var exception = new UserWithFieldAlreadyExistsException(EMAIL);
        var resolver = new ExceptionHandlerMethodResolver(GlobalExceptionHandler.class);
        var resolvedHandler = resolver.resolveMethod(exception);
        var response = handler.handleUserFieldConflict(exception);

        assertEquals("handleUserFieldConflict", resolvedHandler.getName());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Ya existe una cuenta con ese correo electrónico.", response.getBody().getMessage());
    }

    @Test
    void persistenceFailureDoesNotExposeTechnicalDetails() {
        var exception = new FailedOperationException(CREATION_OP, DATABASE_ERR_CONNECTION);
        var response = handler.handleFailedOperation(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("No se pudo guardar la información. Inténtalo nuevamente más tarde.",
                response.getBody().getMessage());
    }

    @Test
    void knownBusinessReasonsAreTranslatedIntoSpanish() {
        var exception = new ResponseStatusException(HttpStatus.CONFLICT, "The destination email is already in use");
        var response = handler.handleResponseStatusException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Ese correo electrónico ya está registrado por otra cuenta.",
                response.getBody().getMessage());
    }
}
