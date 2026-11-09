package com.lectoria_api.chapters.domain.exceptions;

public class InvalidChapterDataException extends RuntimeException {
    public InvalidChapterDataException(String message) {
        super(message);
    }
}
