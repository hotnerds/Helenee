package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.MealTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class DietRemoveFoodRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mealDate;

    private MealTime mealTime;

    private Long foodId;

    @Builder
    public DietRemoveFoodRequestDto(LocalDate mealDate, MealTime mealTime, Long foodId) {
        this.mealDate = mealDate;
        this.mealTime = mealTime;
        this.foodId = foodId;
    }
}
