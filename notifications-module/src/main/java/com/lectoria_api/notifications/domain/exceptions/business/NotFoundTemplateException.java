package com.lectoria_api.notifications.domain.exceptions.business;

public class NotFoundTemplateException extends RuntimeException {
    public NotFoundTemplateException(String message) {
        super(message);
    }
}
