package com.lectoria_api.users.domain.exceptions.business;

import static com.lectoria_api.users.domain.constants.Constants.USER_WITH_FIELD_NOT_EXISTS_MESSAGE;

public class UserWithFieldNotExistsException extends RuntimeException {
    private final String fieldName;

    public UserWithFieldNotExistsException(String fieldName) {
        super(
                String.format(USER_WITH_FIELD_NOT_EXISTS_MESSAGE, fieldName)
        );
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
