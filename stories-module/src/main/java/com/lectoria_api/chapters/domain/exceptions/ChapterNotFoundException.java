package com.lectoria_api.chapters.domain.exceptions;

public class ChapterNotFoundException extends RuntimeException {
    public ChapterNotFoundException() {
        super("Chapter not found");
    }
}
