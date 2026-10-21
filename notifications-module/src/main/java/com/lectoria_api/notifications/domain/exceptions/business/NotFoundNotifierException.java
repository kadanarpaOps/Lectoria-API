package com.lectoria_api.notifications.domain.exceptions.business;

public class NotFoundNotifierException extends RuntimeException {
    public NotFoundNotifierException(String message) {
        super(message);
    }
}
