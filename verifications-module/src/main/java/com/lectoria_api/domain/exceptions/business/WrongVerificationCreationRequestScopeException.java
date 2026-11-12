package com.lectoria_api.domain.exceptions.business;

import static com.lectoria_api.domain.constants.Constants.WRONG_VERIFICATION_CREATION_SCOPE;

public class WrongVerificationCreationRequestScopeException extends RuntimeException {
    private final String scope;

    public WrongVerificationCreationRequestScopeException(String scope) {
        super(String.format(WRONG_VERIFICATION_CREATION_SCOPE, scope));
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }
}
