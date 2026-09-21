package com.lectoria_api.users.domain.exceptions.business;

import static com.lectoria_api.users.domain.constants.Constants.SAME_VALUE_ON_UPDATE_MESSAGE;

public class SameFieldValueOnUpdateException extends RuntimeException {
    public SameFieldValueOnUpdateException(String field) {
        super(
                String.format(SAME_VALUE_ON_UPDATE_MESSAGE, field)
        );
    }
}
