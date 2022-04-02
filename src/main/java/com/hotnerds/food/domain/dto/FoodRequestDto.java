package com.hotnerds.food.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FoodRequestDto {

    private Long foodId;

    private Long amount;

    @Builder
    public FoodRequestDto(Long foodId, Long amount) {
        this.foodId = foodId;
        this.amount = amount;
    }
}
