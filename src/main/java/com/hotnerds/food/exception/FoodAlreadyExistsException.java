package com.hotnerds.food.exception;

public class FoodAlreadyExistsException extends RuntimeException{
    private static final String FOOD_ALREADY_EXISTS_MESSAGE = "해당 음식이 이미 존재합니다.";

    public FoodAlreadyExistsException() {
        super(FOOD_ALREADY_EXISTS_MESSAGE);
    }
}
