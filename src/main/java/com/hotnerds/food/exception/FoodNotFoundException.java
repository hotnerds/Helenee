package com.hotnerds.food.exception;

import com.hotnerds.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FoodNotFoundException extends ApplicationException {

    public static final String ERROR_CODE = "F0000";
    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    public static final String MESSAGE = "좋아요 요청이 중복되었습니다.";

    public FoodNotFoundException() {
        this(ERROR_CODE,HTTP_STATUS, MESSAGE);
    }

    protected FoodNotFoundException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
