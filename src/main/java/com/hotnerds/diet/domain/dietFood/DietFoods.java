package com.hotnerds.diet.domain.dietFood;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DietFoods {

    @OneToMany(
            mappedBy = "diet",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<DietFood> dietFoods = new ArrayList<>();

    public static DietFoods empty() {
        return new DietFoods();
    }

    public List<Food> getFoods() {
        return dietFoods.stream()
                .map(DietFood::getFood)
                .collect(Collectors.toList());
    }

    public void associate(Diet diet, Food food, Long amount) {
        DietFood dietFood = DietFood.builder()
                .diet(diet)
                .food(food)
                .amount(amount)
                .build();

        dietFoods.stream().filter(dietFood::equals)
                .findAny()
                .ifPresent(dietFoods::remove);

        dietFoods.add(dietFood);
    }

    public void clear() {
        dietFoods.clear();
    }

    public Nutrient calculateTotalNutrient() {
        Nutrient totalNutrient = Nutrient.builder()
                .calories(0.0)
                .carbs(0.0)
                .protein(0.0)
                .fat(0.0)
                .build();

        for(DietFood dietFood : dietFoods) {
            Nutrient nutrient = dietFood.getFood()
                    .getNutrient();
            Double amount = dietFood.getAmount()
                    .doubleValue();
            totalNutrient = totalNutrient.plus(
                    nutrient.multiply(amount)
            );
        }

        return totalNutrient;
    }
}
