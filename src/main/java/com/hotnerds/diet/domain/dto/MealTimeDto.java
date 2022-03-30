package com.hotnerds.diet.domain.dto;

import com.hotnerds.diet.domain.MealTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MealTimeDto {
    private String key;
    private String value;

    @Builder
    public MealTimeDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static MealTimeDto of(MealTime mealTime) {
        return MealTimeDto.builder()
                .key(mealTime.getKey())
                .value(mealTime.getValue())
                .build();
    }
}
