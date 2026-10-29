package com.lectoria_api.stories.domain.exceptions;

public class InvalidStoryDataException extends RuntimeException {
    public InvalidStoryDataException(String message) {
        super(message);
    }
}
