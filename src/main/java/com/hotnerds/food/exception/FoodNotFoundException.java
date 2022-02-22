package com.hotnerds.food.exception;

public class FoodNotFoundException extends RuntimeException {

    private static final String FOOD_NOT_FOUND_MESSAGE = "해당 음식이 존재하지 않습니다.";

    public FoodNotFoundException() {
        super(FOOD_NOT_FOUND_MESSAGE);
    }
}
