package com.lectoria_api.stories.domain.exceptions;

public class StoryTitleAlreadyExistsException extends RuntimeException {
    public StoryTitleAlreadyExistsException() {
        super("A story with this title already exists");
    }
}
