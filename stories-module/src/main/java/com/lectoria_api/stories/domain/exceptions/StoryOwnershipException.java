package com.lectoria_api.stories.domain.exceptions;

public class StoryOwnershipException extends RuntimeException {
    public StoryOwnershipException() {
        super("You cannot modify another user's story");
    }
}
