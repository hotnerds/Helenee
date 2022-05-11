package com.hotnerds.user.domain.dto;

import com.hotnerds.user.domain.goal.Goal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class GoalResponseDto {
    private final Double calories;

    private final Double carbs;

    private final Double protein;

    private final Double fat;

    private final LocalDate date;

    private final LocalDateTime lastModifiedTime;

    @Builder
    public GoalResponseDto(Double calories, Double carbs, Double protein, Double fat, LocalDate date, LocalDateTime lastModifiedTime) {
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.date = date;
        this.lastModifiedTime = lastModifiedTime;
    }

    public static GoalResponseDto of(Goal goal) {
        return GoalResponseDto.builder()
                .calories(goal.getCalories())
                .carbs(goal.getCarbs())
                .protein(goal.getProtein())
                .fat(goal.getFat())
                .date(goal.getDate())
                .lastModifiedTime(goal.getLastModifiedAt())
                .build();
    }
}
