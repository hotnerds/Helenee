package com.hotnerds.user.domain.dto;

import com.hotnerds.user.domain.goal.Goal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GoalRequestDto {
    private final Double calories;

    private final Double carbs;

    private final Double protein;

    private final Double fat;

    private final LocalDate date;

    @Builder
    public GoalRequestDto(Double calories, Double carbs, Double protein, Double fat, LocalDate date) {
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.date = date;
    }

    public Goal toEntity() {
        return Goal.builder()
                .calories(calories)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .date(date)
                .build();
    }
}
