package com.lectoria_api.users.domain.exceptions.business;

public class UserRegistrationConflictException extends RuntimeException {
    public UserRegistrationConflictException() {
        super("El nombre de usuario o el correo electrónico ya está asociado a una cuenta.");
    }
}
