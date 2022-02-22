package com.hotnerds.food.domain.dto;

import com.hotnerds.food.domain.Food;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodRequestDto {

    private String name;
    private Double calories;
    private Double carbs;
    private Double protein;
    private Double fat;

    @Builder
    public FoodRequestDto(String name, Double calories, Double carbs, Double protein, Double fat) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public Food toEntity() {
        return Food.builder()
            .name(this.getName())
            .calories(this.getCalories())
            .carbs(this.getCarbs())
            .protein(this.getProtein())
            .fat(this.getFat())
            .build();
    }
}
