package com.hotnerds.followers.exception;

public class FollowerRelationshipExistsException extends RuntimeException {
    public FollowerRelationshipExistsException() {
        super();
    }

    public FollowerRelationshipExistsException(String message) {
        super(message);
    }

    public FollowerRelationshipExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
