package com.hotnerds.food.domain;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.Nutrient;
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
    @Column(name = "FOOD_ID")
    private Long foodId;

    @Column(name = "FOOD_NAME", unique = true)
    private String name;

    @Embedded
    private Nutrient nutrient;

    @ManyToOne
    @JoinColumn(name = "DIET_ID")
    private Diet diet;

    @Builder
    public Food(String name, Nutrient nutrient, Diet diet) {
        this.name = name;
        this.nutrient = nutrient;
        this.diet = diet;
    }
}
