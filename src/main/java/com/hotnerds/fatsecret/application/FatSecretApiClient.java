package com.hotnerds.fatsecret.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.FatSecretConfig;
import com.hotnerds.common.FatSecretToken;

import java.net.URI;
import java.util.Map;

import com.hotnerds.fatsecret.exception.FatSecretResponseErrorException;
import com.hotnerds.fatsecret.exception.FatSecretResponseErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FatSecretApiClient {

    private final String API_URI_PREFIX = "https://platform.fatsecret.com/rest/server.api";

    private final RestTemplate restTemplate;

    private final FatSecretToken fatSecretToken;

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    public FatSecretApiClient(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper, FatSecretToken fatSecretToken) {
        this.fatSecretToken = fatSecretToken;
        this.restTemplate = restTemplateBuilder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .errorHandler(new FatSecretResponseErrorHandler(objectMapper))
                .build();
    }

    public ResponseEntity<Map<String, Object>> searchFoodById(final Long foodId) throws RestClientResponseException {
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
        headers.setBearerAuth(fatSecretToken.getToken());

        RequestEntity<Void> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .build();

        return restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
    }

    public ResponseEntity<Map<String, Object>> searchFoods(final String foodName, final int pageNumber, final int pageSize) throws RestClientResponseException {
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
        headers.setBearerAuth(fatSecretToken.getToken());

        RequestEntity<Void> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .build();

        return restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
    }


}
