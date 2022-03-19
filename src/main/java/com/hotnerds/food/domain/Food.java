package com.hotnerds.food.domain;

import com.hotnerds.diet.domain.Diet;
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
    @GeneratedValue
    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "food_name", unique = true)
    private String foodName;

    @Embedded
    private Nutrient nutrient;

    @Builder
    public Food(String foodName, Nutrient nutrient) {
        this.foodName = foodName;
        this.nutrient = nutrient;
    }
}
