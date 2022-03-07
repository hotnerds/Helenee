package com.hotnerds.fatsecret.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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

    public FatSecretResponseErrorHandler() {
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
        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseEntity = objectMapper.readValue(responseBody, Map.class);
        return responseEntity.containsKey("error");
    }

    protected void handleError(byte[] responseBody) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE); // @JsonRootName annotation 사용을 위한 설정

        FatSecretResponseError error = objectMapper.readValue(responseBody, FatSecretResponseError.class);

        throw new FatSecretResponseErrorException(error.getMessage());
    }
}
