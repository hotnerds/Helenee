package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.MealTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DietSaveFoodRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mealDate;

    private MealTime mealTime;

    private List<Long> foodIds;

    @Builder
    public DietSaveFoodRequestDto(LocalDate mealDate, MealTime mealTime, List<Long> foodIds) {
        this.mealDate = mealDate;
        this.mealTime = mealTime;
        this.foodIds = foodIds;
    }
}
