package com.hotnerds.food.domain;

import com.hotnerds.diet.domain.DietFood;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food {
    @Id
    @GeneratedValue
    @Column(name = "FOOD_ID")
    private Long foodId;

    @Column(unique = true)
    private String name;

    @Column
    private Double calories;

    @Column
    private Double carbs;

    @Column
    private Double protein;

    @Column
    private Double fat;

    @OneToMany
    @JoinColumn(name = "DIETFOOD_ID")
    private List<DietFood> dietFoodList;

    @Builder
    public Food(String name, Double calories, Double carbs, Double protein, Double fat) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }
}
