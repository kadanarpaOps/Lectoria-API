package com.lectoria_api.stories.domain.exceptions;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException() {
        super("Genre not found");
    }
}
