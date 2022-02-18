package com.hotnerds.diet.domain;

import com.hotnerds.food.domain.Food;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietFood {
    @Id
    @GeneratedValue
    @Column(name = "DIETFOOD_ID")
    private Long dietFoodId;

    @ManyToOne
    @JoinColumn(name = "DIET_ID")
    Diet diet;

    @ManyToOne
    @JoinColumn(name = "FOOD_ID")
    Food food;
}
