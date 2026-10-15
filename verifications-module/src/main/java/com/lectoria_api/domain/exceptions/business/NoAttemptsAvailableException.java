package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.NO_AVAILABLE_ATTEMPTS_MESSAGE;

public class NoAttemptsAvailableException extends RuntimeException {
    public NoAttemptsAvailableException() {
        super(NO_AVAILABLE_ATTEMPTS_MESSAGE);
    }
}
