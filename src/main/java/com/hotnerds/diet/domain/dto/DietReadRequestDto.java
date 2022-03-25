package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.MealTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class DietReadRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mealDate;

    private MealTime mealTime;

    @Builder
    public DietReadRequestDto(LocalDate mealDate, MealTime mealTime) {
        this.mealDate = mealDate;
        this.mealTime = mealTime;
    }
}
