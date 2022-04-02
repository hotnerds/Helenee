package com.hotnerds.diet.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class DietRequestByDateDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mealDate;

    @Builder
    public DietRequestByDateDto(LocalDate mealDate) {
        this.mealDate = mealDate;
    }
}
