package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.WRONG_CODE_MESSAGE;

public class VerificationInvalidCodeException extends RuntimeException {
    public VerificationInvalidCodeException() {
        super(WRONG_CODE_MESSAGE);
    }
}
