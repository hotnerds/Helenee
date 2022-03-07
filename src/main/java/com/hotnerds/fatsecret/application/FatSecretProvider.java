package com.hotnerds.fatsecret.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.FatSecretConfig;
import com.hotnerds.fatsecret.domain.dto.FatSecretDetailResponseDto;
import com.hotnerds.fatsecret.domain.dto.FatSecretFood;
import com.hotnerds.fatsecret.domain.dto.FoodWrapper;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import com.hotnerds.fatsecret.exception.FatSecretResponseError;
import com.hotnerds.fatsecret.exception.FatSecretResponseErrorException;
import com.hotnerds.fatsecret.exception.FatSecretResponseErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FatSecretProvider {

    private final String API_URI_PREFIX = "https://platform.fatsecret.com/rest/server.api";

    private final String TOKEN_REQUEST_API_URI_PREFIX = "https://oauth.fatsecret.com/connect/token";

    private final RestTemplate restTemplate;

    private final FatSecretConfig fatSecretConfig;

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    public FatSecretProvider(RestTemplateBuilder restTemplateBuilder, FatSecretConfig fatSecretConfig, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .errorHandler(new FatSecretResponseErrorHandler(objectMapper))
                .build();
        this.fatSecretConfig = fatSecretConfig;
    }

    public ResponseEntity<Map<String, Object>> searchFoodById(final Long foodId) throws FatSecretResponseErrorException {
        final String METHOD = "food.get.v2";
        final String FORMAT = "json";

        URI url = UriComponentsBuilder
                .fromHttpUrl(API_URI_PREFIX)
                .queryParam("method", METHOD)
                .queryParam("food_id", foodId.toString())
                .queryParam("format", FORMAT)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(fatSecretConfig.getToken());

        RequestEntity<Void> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .build();

        return restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
    }

    public ResponseEntity<Map<String, Object>> searchFoods(final String foodName, final int pageNumber, final int pageSize) throws FatSecretResponseErrorException {
        final String METHOD = "food.get.v2";
        final String FORMAT = "json";

        URI url = UriComponentsBuilder
                .fromHttpUrl(API_URI_PREFIX)
                .queryParam("method", METHOD)
                .queryParam("search_expression", foodName)
                .queryParam("page_number", pageNumber)
                .queryParam("max_results", pageSize)
                .queryParam("format", FORMAT)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(fatSecretConfig.getToken());

        RequestEntity<Void> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .build();

        return restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
    }

    public ResponseEntity<Map<String, Object>> getAccessToken() throws FatSecretResponseErrorException {
        URI url = UriComponentsBuilder
                .fromHttpUrl(TOKEN_REQUEST_API_URI_PREFIX)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(fatSecretConfig.getId(), fatSecretConfig.getSecret());

        RequestEntity<Void> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .build();

        return restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
    }


}
