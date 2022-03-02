package com.hotnerds.follow.exception;

public class FollowRelationshipNotFound extends RuntimeException {
    public FollowRelationshipNotFound() {
        super();
    }

    public FollowRelationshipNotFound(String message) {
        super(message);
    }

    public FollowRelationshipNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
