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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "food_name", unique = true)
    private String foodName;

    @Embedded
    private Nutrient nutrient;

    @Builder
    public Food(Long foodId, String foodName, Nutrient nutrient) {
        this.id = foodId;
        this.foodName = foodName;
        this.nutrient = nutrient;
    }
}
