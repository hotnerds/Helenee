package com.hotnerds.food.domain.apiclient;

import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FoodApiClient {

    Food searchFoodById(Long foodId);

    List<Food> searchFoods(String foodName, int pageNumber, int pageSize);
}
