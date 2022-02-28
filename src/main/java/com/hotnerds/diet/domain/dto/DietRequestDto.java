package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.Food;
import com.hotnerds.diet.domain.MealDateTime;
import com.hotnerds.diet.domain.Nutrient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietRequestDto {

    private MealDateTime mealDateTime;

    private Nutrient nutrient;

    private Long userId;

    private List<Food> foodList;

    @Builder
    public DietRequestDto(MealDateTime mealDateTime, Nutrient nutrient, Long userId, List<Food> foodList) {
        this.mealDateTime = mealDateTime;
        this.nutrient = nutrient;
        this.userId = userId;
        this.foodList = foodList;
    }
}
