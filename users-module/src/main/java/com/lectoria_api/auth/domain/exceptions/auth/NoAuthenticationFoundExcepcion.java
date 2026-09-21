package com.lectoria_api.auth.domain.exceptions.auth;

import static com.lectoria_api.auth.domain.constants.Constants.NOT_AUTH_FOUND;

public class NoAuthenticationFoundExcepcion extends RuntimeException {
    public NoAuthenticationFoundExcepcion() {
        super(
                String.format(NOT_AUTH_FOUND)
        );
    }
}
