package com.lectoria_api.users.domain.exceptions.business;

import com.lectoria_api.users.domain.constants.Constants;

public class FailedOperationException extends RuntimeException {
    private final String operation;
    private final String reason;

    public FailedOperationException(String operation, String cause) {
        super(
                String.format(Constants.FAILED_OP_MESSAGE, operation, cause)
        );
        this.operation = operation;
        this.reason = cause;
    }

    public String getOperation() {
        return operation;
    }

    public String getReason() {
        return reason;
    }
}
