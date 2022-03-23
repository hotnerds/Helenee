package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.MealTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class DietAddFoodRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mealDate;

    private MealTime mealTime;

    private Long apiId;

    @Builder
    public DietAddFoodRequestDto(LocalDate mealDate, MealTime mealTime, Long apiId) {
        this.mealDate = mealDate;
        this.mealTime = mealTime;
        this.apiId = apiId;
    }
}
