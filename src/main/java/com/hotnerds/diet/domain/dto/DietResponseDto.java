package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.Diet;
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
public class DietResponseDto {

    private Long dietId;

    private MealDateTime mealDateTime;

    private Nutrient nutrient;

    private Long userId;

    private List<Food> foodList;

    @Builder
    public DietResponseDto(Long dietId, MealDateTime mealDateTime, Nutrient nutrient, Long userId, List<Food> foodList) {
        this.dietId = dietId;
        this.mealDateTime = mealDateTime;
        this.nutrient = nutrient;
        this.userId = userId;
        this.foodList = foodList;
    }

    public static DietResponseDto of(Diet diet) {
        return DietResponseDto.builder()
                .dietId(diet.getDietId())
                .mealDateTime(diet.getMealDateTime())
                .nutrient(diet.getNutrient())
                .userId(diet.getUser().getId())
                .foodList(diet.getFoodList())
                .build();
    }
}
