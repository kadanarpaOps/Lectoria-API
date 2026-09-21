package com.lectoria_api.users.domain.exceptions.business;

import com.lectoria_api.users.domain.constants.Constants;

public class FailedOperationException extends RuntimeException {
    public FailedOperationException(String operation, String cause) {
        super(
                String.format(Constants.FAILED_OP_MESSAGE, operation, cause)
        );
    }
}
