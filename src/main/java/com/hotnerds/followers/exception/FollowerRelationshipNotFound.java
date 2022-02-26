package com.hotnerds.followers.exception;

public class FollowerRelationshipNotFound extends RuntimeException {
    public FollowerRelationshipNotFound() {
        super();
    }

    public FollowerRelationshipNotFound(String message) {
        super(message);
    }

    public FollowerRelationshipNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
