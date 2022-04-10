package com.hotnerds.food.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodRequestByNameDto {

    private String foodName;

    private Pageable pageAble;

    @Builder
    public FoodRequestByNameDto(String foodName, Pageable pageRequest) {
        this.foodName = foodName;
        this.pageAble = pageRequest;
    }
}
