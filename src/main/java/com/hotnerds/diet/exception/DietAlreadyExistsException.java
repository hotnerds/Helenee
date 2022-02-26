package com.hotnerds.diet.exception;

public class DietAlreadyExistsException extends RuntimeException {
    private static final String DIET_EXISTS_EXCEPTION_MESSAGE = "동일한 정보를 가진 식단이 이미 존재합니다.";

    public DietAlreadyExistsException() {
        super(DIET_EXISTS_EXCEPTION_MESSAGE);
    }
}
