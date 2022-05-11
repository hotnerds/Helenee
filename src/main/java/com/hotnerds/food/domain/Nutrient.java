package com.hotnerds.food.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nutrient {

    @Column(name = "calories")
    private Double calories;

    @Column(name = "carbs")
    private Double carbs;

    @Column(name = "protein")
    private Double protein;

    @Column(name = "fat")
    private Double fat;

    @Builder
    public Nutrient(Double calories, Double carbs, Double protein, Double fat) {
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public Nutrient plus(Nutrient nutrient) {
        return Nutrient.builder()
                .calories(this.calories + nutrient.getCalories())
                .carbs(this.carbs + nutrient.getCarbs())
                .protein(this.protein + nutrient.getProtein())
                .fat(this.fat + nutrient.getFat())
                .build();
    }

    public Nutrient multiply(Double value) {
        return Nutrient.builder()
                .calories(this.calories * value)
                .carbs(this.carbs * value)
                .protein(this.protein * value)
                .fat(this.fat * value)
                .build();
    }
}
