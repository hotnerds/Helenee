package com.hotnerds.post.exception;

import com.hotnerds.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ApplicationException {

    public static final String ERROR_CODE = "P0001";
    public static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    public static final String MESSAGE = "해당하는 게시물을 찾을 수 없습니다.";

    public PostNotFoundException() {
        this(ERROR_CODE,HTTP_STATUS, MESSAGE);
    }

    public PostNotFoundException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }


}
