package com.lectoria_api.auth.domain.exceptions.auth;

import static com.lectoria_api.auth.domain.constants.Constants.NOT_AUTH_FOUND;

public class NoAuthenticationFoundException extends RuntimeException {
    public NoAuthenticationFoundException() {
        super(
                String.format(NOT_AUTH_FOUND)
        );
    }
}
