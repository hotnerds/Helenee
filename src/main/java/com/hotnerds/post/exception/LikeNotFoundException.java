package com.hotnerds.post.exception;

import com.hotnerds.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class LikeNotFoundException extends ApplicationException {
    public static final String ERROR_CODE = "";
    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    public static final String MESSAGE = "게시물에 좋아요를 누르지 않았습니다.";

    public LikeNotFoundException() {
        this(ERROR_CODE,HTTP_STATUS, MESSAGE);
    }

    protected LikeNotFoundException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
