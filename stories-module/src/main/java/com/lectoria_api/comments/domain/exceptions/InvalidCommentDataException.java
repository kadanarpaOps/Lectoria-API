package com.lectoria_api.comments.domain.exceptions;

public class InvalidCommentDataException extends RuntimeException {
    public InvalidCommentDataException(String message) {
        super(message);
    }
}
