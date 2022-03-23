package com.hotnerds.food.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "api_id", unique = true)
    private Long apiId;

    @Column(name = "food_name", unique = true)
    private String foodName;

    @Embedded
    private Nutrient nutrient;

    @Builder
    public Food(Long apiId, String foodName, Nutrient nutrient) {
        this.apiId = apiId;
        this.foodName = foodName;
        this.nutrient = nutrient;
    }
}
