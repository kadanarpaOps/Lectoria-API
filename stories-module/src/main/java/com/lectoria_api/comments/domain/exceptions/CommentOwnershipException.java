package com.lectoria_api.comments.domain.exceptions;

public class CommentOwnershipException extends RuntimeException {
    public CommentOwnershipException() {
        super("You do not own this comment");
    }
}
