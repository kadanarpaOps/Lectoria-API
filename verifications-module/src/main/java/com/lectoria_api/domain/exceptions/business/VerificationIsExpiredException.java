package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.VERIFICATION_HAS_EXPIRED_MESSAGE;

public class VerificationIsExpiredException extends RuntimeException {
    public VerificationIsExpiredException() {
        super(VERIFICATION_HAS_EXPIRED_MESSAGE);
    }
}
