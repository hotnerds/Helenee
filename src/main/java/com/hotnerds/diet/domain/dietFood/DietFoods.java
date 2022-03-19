package com.hotnerds.diet.domain.dietFood;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.food.domain.Food;
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

    public void associate(Diet diet, Food food) {
        dietFoods.add(DietFood.builder()
                        .diet(diet)
                        .food(food)
                .build());
    }
}
