package com.hotnerds.diet.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotnerds.diet.domain.MealTime;
import lombok.*;
import org.apache.tomcat.jni.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
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
