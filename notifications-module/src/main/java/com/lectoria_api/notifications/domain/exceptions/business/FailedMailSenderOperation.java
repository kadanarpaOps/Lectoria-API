package com.lectoria_api.notifications.domain.exceptions.business;

public class FailedMailSenderOperation extends RuntimeException {
    public FailedMailSenderOperation(String message) {
        super(message);
    }
}
