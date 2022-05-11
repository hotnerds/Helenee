package com.hotnerds.food.domain.dto;

import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FoodResponseDto {

    private Long foodId;

    private String foodName;

    private Nutrient nutrient;

    @Builder
    public FoodResponseDto(Long foodId, String foodName, Nutrient nutrient) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.nutrient = nutrient;
    }

    public static FoodResponseDto of(final Food food) {
        return FoodResponseDto.builder()
                .foodId(food.getId())
                .foodName(food.getFoodName())
                .nutrient(food.getNutrient())
                .build();
    }
}
