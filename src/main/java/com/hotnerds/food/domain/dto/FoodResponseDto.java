package com.hotnerds.food.domain.dto;

import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FoodResponseDto {

    private Long foodId;

    private Long apiId;

    private String foodName;

    private Nutrient nutrient;

    @Builder
    public FoodResponseDto(Long foodId, Long apiId, String foodName, Nutrient nutrient) {
        this.foodId = foodId;
        this.apiId = apiId;
        this.foodName = foodName;
        this.nutrient = nutrient;
    }

    public static FoodResponseDto of(final Food food) {
        return FoodResponseDto.builder()
                .foodId(food.getFoodId())
                .apiId(food.getApiId())
                .foodName(food.getFoodName())
                .nutrient(food.getNutrient())
                .build();
    }
}
