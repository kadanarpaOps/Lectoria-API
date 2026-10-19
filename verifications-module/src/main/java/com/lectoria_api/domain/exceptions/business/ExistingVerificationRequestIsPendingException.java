package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.EXISTING_VERIFICATION_IS_PENDING_MESSAGE;

public class ExistingVerificationRequestIsPendingException extends RuntimeException {
    public ExistingVerificationRequestIsPendingException() {
        super(EXISTING_VERIFICATION_IS_PENDING_MESSAGE);
    }
}
