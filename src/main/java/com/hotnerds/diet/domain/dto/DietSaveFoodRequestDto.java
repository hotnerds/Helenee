package com.hotnerds.diet.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.food.domain.dto.FoodRequestDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
public class DietSaveFoodRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate mealDate;

    private MealTime mealTime;

    private List<FoodRequestDto> foods;

    @Builder
    public DietSaveFoodRequestDto(LocalDate mealDate, MealTime mealTime, List<FoodRequestDto> foods) {
        this.mealDate = mealDate;
        this.mealTime = mealTime;
        this.foods = foods;
    }
}
