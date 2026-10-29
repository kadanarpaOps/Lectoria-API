package com.lectoria_api.chapters.domain.exceptions;

public class ChapterOwnershipException extends RuntimeException {
    public ChapterOwnershipException() {
        super("You do not own this chapter");
    }
}
