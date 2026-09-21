package com.lectoria_api.users.domain.exceptions.business;

import static com.lectoria_api.users.domain.constants.Constants.USER_WITH_FIELD_ALREADY_EXISTS_MESSAGE;

public class UserWithFieldAlreadyExistsException extends RuntimeException {
    public UserWithFieldAlreadyExistsException(String fieldName) {
        super(
                String.format(USER_WITH_FIELD_ALREADY_EXISTS_MESSAGE, fieldName)
        );
    }
}
