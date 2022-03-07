package com.hotnerds.fatsecret.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import javax.swing.text.html.Option;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class FatSecretResponseErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Autowired
    public FatSecretResponseErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return super.hasError(response) || hasError(getResponseBody(response));
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Optional<HttpStatus> optionalStatusCode = Optional.ofNullable(HttpStatus.resolve(response.getRawStatusCode()))
                .filter(HttpStatus::is2xxSuccessful);

        if(optionalStatusCode.isPresent()) {
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
        FatSecretResponseError error = objectMapper.readValue(responseBody, ErrorWrapper.class)
                .getError();

        throw new FatSecretResponseErrorException(error.getMessage());
    }
}
