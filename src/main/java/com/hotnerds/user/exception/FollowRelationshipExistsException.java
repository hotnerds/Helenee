package com.hotnerds.user.exception;

public class FollowRelationshipExistsException extends RuntimeException {
    public FollowRelationshipExistsException() {
        super();
    }

    public FollowRelationshipExistsException(String message) {
        super(message);
    }

    public FollowRelationshipExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
