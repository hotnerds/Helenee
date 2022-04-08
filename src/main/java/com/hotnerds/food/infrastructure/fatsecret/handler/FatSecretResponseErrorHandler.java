package com.hotnerds.food.infrastructure.fatsecret.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class FatSecretResponseErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public FatSecretResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return super.hasError(response) || this.hasError(getResponseBody(response));
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Optional<HttpStatus> optionalStatusCode = Optional.ofNullable(HttpStatus.resolve(response.getRawStatusCode()))
                .filter(HttpStatus::is2xxSuccessful);

        if (optionalStatusCode.isPresent()) {
            handleError(getResponseBody(response));
        } else {
            super.handleError(response);
        }
    }

    protected boolean hasError(byte[] responseBody) throws IOException {
        Map<String, Object> responseEntity = objectMapper.readValue(responseBody, Map.class);
        return responseEntity.containsKey("error");
    }

    protected void handleError(byte[] responseBody) throws IOException {
        throw new BusinessException(ErrorCode.EXTERNAL_COMMUNICATION_EXCEPTION);
    }
}
