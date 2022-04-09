package com.hotnerds.food.infrastructure.fatsecret;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.apiclient.FoodApiClient;
import com.hotnerds.food.infrastructure.fatsecret.dto.getfood.FoodWrapper;
import com.hotnerds.food.infrastructure.fatsecret.dto.searchfoods.FoodsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FatSecretApiClient implements FoodApiClient {

    private static final String METHOD = "method";
    private static final String FORMAT = "format";
    private static final String JSON_FORMAT = "json";
    @Value("${fat-secret.api-url}")
    private String API_URI_PREFIX;
    private final RestTemplate restTemplate;

    private final FatSecretToken fatSecretToken;

    @Autowired
    public FatSecretApiClient(RestTemplateBuilder restTemplateBuilder, FatSecretToken fatSecretToken) {
        this.fatSecretToken = fatSecretToken;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Food searchFoodById(final Long foodId) throws RestClientResponseException {
        final String FOOD_GET_METHOD = "food.get.v2";
        final String FOOD_ID = "food_id";

        URI url = UriComponentsBuilder
                .fromHttpUrl(API_URI_PREFIX)
                .queryParam(METHOD, FOOD_GET_METHOD)
                .queryParam(FOOD_ID, foodId.toString())
                .queryParam(FORMAT, JSON_FORMAT)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(fatSecretToken.getToken());

        RequestEntity<Void> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .build();

        ResponseEntity<FoodWrapper> response = restTemplate.exchange(request, FoodWrapper.class);

        FoodWrapper responseBody = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_COMMUNICATION_EXCEPTION));

        return responseBody
                .getFood()
                .toEntity();
    }

    @Override
    public List<Food> searchFoods(final String foodName, final int pageNumber, final int pageSize) throws RestClientResponseException {
        final String FOODS_SEARCH_METHOD = "foods.search";
        final String SEARCH_EXPRESSION = "search_expression";
        final String PAGE_NUMBER = "page_number";
        final String MAX_RESULTS = "max_results";

        URI url = UriComponentsBuilder
                .fromHttpUrl(API_URI_PREFIX)
                .queryParam(METHOD, FOODS_SEARCH_METHOD)
                .queryParam(SEARCH_EXPRESSION, foodName)
                .queryParam(PAGE_NUMBER, pageNumber)
                .queryParam(MAX_RESULTS, pageSize)
                .queryParam(FORMAT, JSON_FORMAT)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(fatSecretToken.getToken());

        RequestEntity<Void> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .build();

        ResponseEntity<FoodsWrapper> response = restTemplate.exchange(request, FoodsWrapper.class);

        FoodsWrapper responseBody = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_COMMUNICATION_EXCEPTION));

        return responseBody
                .getFoods()
                .getFoodList()
                .stream()
                .map(FoodsWrapper.SearchFoodsResponse::toEntity).collect(Collectors.toList());
    }


}
