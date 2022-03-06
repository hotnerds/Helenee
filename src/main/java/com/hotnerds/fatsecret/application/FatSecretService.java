package com.hotnerds.fatsecret.application;

import com.hotnerds.common.FatSecretConfig;
import com.hotnerds.fatsecret.domain.dto.FatSecretDetailResponseDto;
import com.hotnerds.fatsecret.domain.dto.FatSecretFood;
import com.hotnerds.fatsecret.domain.dto.FoodWrapper;
import java.net.URI;

import com.hotnerds.fatsecret.exception.FatSecretResponseErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FatSecretService {

    private final String API_URI_PREFIX = "https://platform.fatsecret.com/rest/server.api";

    private final RestTemplate restTemplate;

    private final FatSecretConfig fatSecretConfig;

    @Autowired
    public FatSecretService(RestTemplate restTemplate, FatSecretConfig fatSecretConfig) {
        this.restTemplate = restTemplate;
        this.fatSecretConfig = fatSecretConfig;
    }

    public FatSecretDetailResponseDto getFoodById(Long foodId) throws FatSecretResponseErrorException {
        final String METHOD = "food.get.v2";
        final String FOOD_ID = foodId.toString();
        final String FORMAT = "json";

        final String token = fatSecretConfig.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity httpEntity = new HttpEntity(headers);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("method", METHOD);
        params.add("food_id", FOOD_ID);
        params.add("format", FORMAT);

        URI uri = UriComponentsBuilder
            .fromHttpUrl(API_URI_PREFIX)
            .queryParams(params)
            .build()
            .toUri();

        ResponseEntity<FoodWrapper> responseEntity = restTemplate.exchange(uri,
            HttpMethod.POST, httpEntity, FoodWrapper.class);

        FatSecretFood fatSecretFood = responseEntity.getBody().getFatSecretFood();
        return FatSecretDetailResponseDto.of(fatSecretFood);
    }

}
