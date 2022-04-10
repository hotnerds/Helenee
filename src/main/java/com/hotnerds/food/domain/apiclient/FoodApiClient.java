package com.hotnerds.food.domain.apiclient;

import com.hotnerds.food.domain.Food;

import java.util.List;

public interface FoodApiClient {

    Food searchFoodById(Long foodId);

    List<Food> searchFoods(String foodName, int pageNumber, int pageSize);
}
