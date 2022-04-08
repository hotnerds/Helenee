package com.hotnerds.food.infrastructure.exception;

import lombok.Getter;

@Getter
public class FatSecretResponseError {
    private Integer code;
    private String message;
}
