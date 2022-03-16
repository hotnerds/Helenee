package com.hotnerds.user.exception;

import com.hotnerds.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    public static final String ERROR_CODE = "P0002";
    public static final HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    public static final String MESSAGE = "해당하는 사용자를 찾을 수 없습니다.";


    public UserNotFoundException() {
        this(ERROR_CODE,HTTP_STATUS, MESSAGE);
    }

    public UserNotFoundException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
