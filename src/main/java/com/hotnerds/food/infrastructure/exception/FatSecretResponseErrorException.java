package com.hotnerds.food.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

public class FatSecretResponseErrorException extends RestClientResponseException {

    private static final String FAT_SECRET_RESPONSE_ERROR_STATUS_MESSAGE = "FatSecret API 호출에 실패했습니다.";

    public FatSecretResponseErrorException(String message) {
        super(FAT_SECRET_RESPONSE_ERROR_STATUS_MESSAGE, HttpStatus.OK.value(), message, null, null, null);
    }
}
