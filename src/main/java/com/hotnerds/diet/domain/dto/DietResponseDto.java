package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DietResponseDto {

    private Long dietId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mealDate;

    private MealTimeDto mealTime;

    private Nutrient totalNutrient;

    private List<FoodResponseDto> foodList;

    @Builder
    public DietResponseDto(Long dietId, LocalDate mealDate, MealTimeDto mealTime, Nutrient totalNutrient, List<FoodResponseDto> foodList) {
        this.dietId = dietId;
        this.mealDate = mealDate;
        this.mealTime = mealTime;
        this.totalNutrient = totalNutrient;
        this.foodList = foodList;
    }

    public static DietResponseDto of(final Diet diet) {
        List<FoodResponseDto> foodList = diet.getFoods()
                .stream()
                .map(FoodResponseDto::of)
                .collect(Collectors.toList());

        return DietResponseDto.builder()
                .dietId(diet.getId())
                .mealDate(diet.getMealDate())
                .mealTime(MealTimeDto.of(diet.getMealTime()))
                .totalNutrient(diet.calculateTotalNutrient())
                .foodList(foodList)
                .build();
    }
}
