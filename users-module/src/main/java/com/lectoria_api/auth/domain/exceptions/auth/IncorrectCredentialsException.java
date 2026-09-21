package com.lectoria_api.auth.domain.exceptions.auth;

import static com.lectoria_api.auth.domain.constants.Constants.INVALID_CREDENTIALS_MESSAGE;

public class IncorrectCredentialsException extends RuntimeException {
    public IncorrectCredentialsException() {
        super(
                String.format(INVALID_CREDENTIALS_MESSAGE)
        );
    }
}
