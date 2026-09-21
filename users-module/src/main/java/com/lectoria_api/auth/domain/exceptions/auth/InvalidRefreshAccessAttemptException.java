package com.lectoria_api.auth.domain.exceptions.auth;

import static com.lectoria_api.auth.domain.constants.Constants.INVALID_REFRESH_ACCESS_ATTEMPT;

public class InvalidRefreshAccessAttemptException extends RuntimeException {
    public InvalidRefreshAccessAttemptException() {
        super(
                String.format(INVALID_REFRESH_ACCESS_ATTEMPT)
        );
    }
}
