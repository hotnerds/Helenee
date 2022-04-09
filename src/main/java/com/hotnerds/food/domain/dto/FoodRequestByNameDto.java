package com.hotnerds.food.domain.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodRequestByNameDto {

    private String foodName;

    private PageRequest pageRequest;

    @Builder
    public FoodRequestByNameDto(String foodName, PageRequest pageRequest) {
        this.foodName = foodName;
        this.pageRequest = pageRequest;
    }
}
