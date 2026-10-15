package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.NOT_FOUND_VERIFICATION_MESSAGE;

public class VerificationNotFoundException extends RuntimeException {
    public VerificationNotFoundException() {
        super(NOT_FOUND_VERIFICATION_MESSAGE);
    }
}
