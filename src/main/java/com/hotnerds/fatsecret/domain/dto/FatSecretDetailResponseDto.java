package com.hotnerds.fatsecret.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FatSecretDetailResponseDto {

    private Long foodApiId;
    private String name;
    private Double calories;
    private Double carbs;
    private Double protein;
    private Double fat;

    @Builder
    public FatSecretDetailResponseDto(Long foodApiId, String name, Double calories, Double protein,
        Double carbs, Double fat) {
        this.foodApiId = foodApiId;
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public static FatSecretDetailResponseDto of(FatSecretFood fatSecretFood) {
        return FatSecretDetailResponseDto.builder()
            .foodApiId(fatSecretFood.getFoodId())
            .name(fatSecretFood.getFoodName())
            .calories(fatSecretFood.getServingWrapper()
                .getFatSecretServingList()
                .get(0)
                .getCalories())
            .carbs(fatSecretFood.getServingWrapper()
                .getFatSecretServingList()
                .get(0)
                .getCarbohydrate())
            .protein(fatSecretFood.getServingWrapper()
                .getFatSecretServingList()
                .get(0)
                .getProtein())
            .fat(fatSecretFood.getServingWrapper()
                .getFatSecretServingList()
                .get(0)
                .getFat())
            .build();
    }

}
