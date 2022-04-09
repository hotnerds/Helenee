package com.hotnerds.food.infrastructure.fatsecret.dto.getfood;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodWrapper {

    @JsonProperty("food")
    private GetFoodResponse food = new GetFoodResponse();

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetFoodResponse {

        @JsonProperty("food_id")
        private Long foodId;

        @JsonProperty("food_name")
        private String foodName;

        @JsonProperty("food_type")
        private String foodType;

        @JsonProperty("food_url")
        private String foodUrl;

        @JsonProperty("servings")
        private ServingWrapper servings;

        public Food toEntity() {
            ServingWrapper.Serving serving = servings.getServingList().get(0);

            return Food.builder()
                    .foodId(foodId)
                    .foodName(foodName)
                    .nutrient(
                            new Nutrient(serving.getCalories(),
                                    serving.getCarbohydrate(),
                                    serving.getProtein(),
                                    serving.getFat())
                    )
                    .build();
        }
    }
}
