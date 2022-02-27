package com.hotnerds.diet.exception;

public class DietNotFoundException extends RuntimeException {
    private static final String DIET_NOT_FOUND_EXCEPTION_MESSAGE = "해당하는 식단이 없습니다.";

    public DietNotFoundException() {
        super(DIET_NOT_FOUND_EXCEPTION_MESSAGE);
    }
}
