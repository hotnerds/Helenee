package com.hotnerds.diet.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;

import java.util.Arrays;
import java.util.Locale;

public enum MealTime {
    BREAKFAST("아침 식사"),
    LUNCH("점심 식사"),
    DINNER("저녁 식사");

    private String value;

    MealTime(String value) {
        this.value = value;
    }

    public String getKey() {
        return name();
    }

    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static MealTime of(String key) {
        return Arrays.stream(MealTime.values())
                    .filter(e -> e.name().equals(key.toUpperCase(Locale.ROOT)))
                    .findAny()
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_MEALTIME_VALUE));
    }
}
