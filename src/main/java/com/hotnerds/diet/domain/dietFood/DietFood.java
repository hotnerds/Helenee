package com.hotnerds.diet.domain.dietFood;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.food.domain.Food;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"diet_id", "food_id"}
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietFood {

    @Id
    @GeneratedValue
    Long dietFoodId;

    @ManyToOne
    @JoinColumn(name = "diet_id")
    Diet diet;

    @ManyToOne
    @JoinColumn(name = "food_id")
    Food food;

    @Builder
    public DietFood(Diet diet, Food food) {
        this.diet = diet;
        this.food = food;
    }
}
