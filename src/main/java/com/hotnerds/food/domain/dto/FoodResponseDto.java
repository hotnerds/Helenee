package com.hotnerds.food.domain.dto;

import com.hotnerds.food.domain.Food;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodResponseDto {

    private Long id;
    private String name;
    private Double calories;
    private Double carbs;
    private Double protein;
    private Double fat;

    @Builder
    public FoodResponseDto(Long id, String name, Double calories, Double carbs, Double protein,
        Double fat) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public static FoodResponseDto of(Food food) {
        return FoodResponseDto.builder()
            .id(food.getFoodId())
            .name(food.getName())
            .calories(food.getCalories())
            .carbs(food.getCarbs())
            .protein(food.getProtein())
            .fat(food.getFat())
            .build();
    }
}
