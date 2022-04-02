package com.hotnerds.diet.domain.dietFood;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.food.domain.Food;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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
    private Long dietFoodId;

    @ManyToOne
    @JoinColumn(name = "diet_id")
    private Diet diet;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(name = "amount")
    private Long amount;

    @Builder
    public DietFood(Diet diet, Food food, Long amount) {
        this.diet = diet;
        this.food = food;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DietFood dietFood = (DietFood) o;
        return Objects.equals(diet, dietFood.diet) && Objects.equals(food, dietFood.food);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diet, food);
    }
}
