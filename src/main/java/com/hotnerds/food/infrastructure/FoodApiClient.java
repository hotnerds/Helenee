package com.hotnerds.food.infrastructure;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface FoodApiClient {

    ResponseEntity<Map<String, Object>> searchFoodById(Long foodId);

    ResponseEntity<Map<String, Object>> searchFoods(String foodName, int pageNumber, int pageSize);
}
