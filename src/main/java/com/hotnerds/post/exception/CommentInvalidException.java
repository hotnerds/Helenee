package com.hotnerds.post.exception;


import com.hotnerds.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CommentInvalidException extends ApplicationException {
    public static final String ERROR_CODE = "";
    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    public static final String MESSAGE = "댓글 생성 요청에 문제가 있습니";

    public CommentInvalidException() {
        this(ERROR_CODE, HTTP_STATUS, MESSAGE);
    }

    protected CommentInvalidException(String error_code, HttpStatus httpStatus, String message) {
        super(error_code, httpStatus, message);
    }
}
