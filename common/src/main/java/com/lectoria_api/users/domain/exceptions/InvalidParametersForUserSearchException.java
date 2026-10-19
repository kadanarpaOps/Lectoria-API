package com.lectoria_api.users.domain.exceptions;

import static com.lectoria_api.users.domain.constants.Constants.INVALID_PARAMETERS_FOR_USER_SEARCH_MESSAGE;

public class InvalidParametersForUserSearchException extends RuntimeException {
    public InvalidParametersForUserSearchException() {
        super(INVALID_PARAMETERS_FOR_USER_SEARCH_MESSAGE);
    }
}
