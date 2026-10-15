package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.VERIFICATION_CODE_HAS_EXPIRED_MESSAGE;

public class VerificationCodeIsExpiredException extends RuntimeException {
    public VerificationCodeIsExpiredException(String message) {
        super(VERIFICATION_CODE_HAS_EXPIRED_MESSAGE);
    }
}
