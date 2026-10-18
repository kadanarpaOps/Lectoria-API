package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.EXISTING_VERIFICATION_IS_PENDING_MESSAGE;
import static com.lectoria_api.domain.constants.Constants.WRONG_VERIFICATION_CREATION_SCOPE;

public class WrongVerificationCreationRequestScopeException extends RuntimeException {
    public WrongVerificationCreationRequestScopeException(String scope) {
        super(String.format(WRONG_VERIFICATION_CREATION_SCOPE, scope));
    }
}
