package com.lectoria_api.stories.domain.exceptions;

public class StoryNotFoundException extends RuntimeException {
    public StoryNotFoundException() {
        super("Story not found");
    }
}
