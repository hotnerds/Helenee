package com.hotnerds.post.exception;

import com.hotnerds.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ApplicationException {
    public static final String ERROR_CODE = "";
    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    public static final String MESSAGE = "해당 정보를 가진 댓글을 찾을 수 없습니다.";

    public CommentNotFoundException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    protected CommentNotFoundException(String error_code, HttpStatus httpStatus, String message) {
        super(error_code, httpStatus, message);
    }
}
