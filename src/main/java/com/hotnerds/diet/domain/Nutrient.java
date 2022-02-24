package com.hotnerds.diet.domain;

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

    @Column(name = "TOTAL_CALORIES", nullable = false)
    private Double totalCalories;

    @Column(name = "TOTAL_CARBS", nullable = false)
    private Double totalCarbs;

    @Column(name = "TOTAL_PROTEIN", nullable = false)
    private Double totalProtein;

    @Column(name = "TOTAL_FAT", nullable = false)
    private Double totalFat;

    @Builder
    public Nutrient(Double totalCalories, Double totalCarbs, Double totalProtein, Double totalFat) {
        this.totalCalories = totalCalories;
        this.totalCarbs = totalCarbs;
        this.totalProtein = totalProtein;
        this.totalFat = totalFat;
    }
}
