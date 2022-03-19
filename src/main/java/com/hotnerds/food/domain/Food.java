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
    private String name;

    @Embedded
    private Nutrient nutrient;

    @ManyToOne
    @JoinColumn(name = "diet_id")
    private Diet diet;

    @Builder
    public Food(String name, Nutrient nutrient, Diet diet) {
        this.name = name;
        this.nutrient = nutrient;
        this.diet = diet;
    }
}
