package com.hotnerds.food.infrastructure.fatsecret.dto.searchfoods;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodsWrapper {

    @JsonProperty("foods")
    Foods foods;

    @Getter
    public static class Foods {

        @JsonProperty("food")
        List<SearchFoodsResponse> foodList;
    }

    @Getter
    public static class SearchFoodsResponse {

        @JsonProperty("food_id")
        private Long foodId;

        @JsonProperty("food_name")
        private String foodName;

        @JsonProperty("food_type")
        private String foodType;

        @JsonProperty("brand_name")
        private String brandName;

        @JsonProperty("food_description")
        private String foodDescription;

        public Double getCalories() {
            Pattern pattern = Pattern.compile("Calories: \\d+(\\.\\d+)?");
            Matcher matcher = pattern.matcher(foodDescription);
            if (!matcher.find()) {
                throw new IllegalStateException();
            }
            return Double.parseDouble(
                    matcher.group().replace("Calories: ", "")
            );
        }

        public Double getCarbohydrate() {
            Pattern pattern = Pattern.compile("Carbs: \\d+(\\.\\d+)?");
            Matcher matcher = pattern.matcher(foodDescription);
            if (!matcher.find()) {
                throw new IllegalStateException();
            }
            return Double.parseDouble(
                    matcher.group().replace("Carbs: ", "")
            );
        }

        public Double getProtein() {
            Pattern pattern = Pattern.compile("Protein: \\d+(\\.\\d+)?");
            Matcher matcher = pattern.matcher(foodDescription);
            if (!matcher.find()) {
                throw new IllegalStateException();
            }
            return Double.parseDouble(
                    matcher.group().replace("Protein: ", "")
            );
        }

        public Double getFat() {
            Pattern pattern = Pattern.compile("Fat: \\d+(\\.\\d+)?");
            Matcher matcher = pattern.matcher(foodDescription);
            if (!matcher.find()) {
                throw new IllegalStateException();
            }
            return Double.parseDouble(
                    matcher.group().replace("Fat: ", "")
            );
        }

        public Food toEntity() {
            return Food.builder()
                    .foodId(foodId)
                    .foodName(foodName)
                    .nutrient(
                            new Nutrient(this.getCalories(),
                                    this.getCarbohydrate(),
                                    this.getProtein(),
                                    this.getFat())
                    )
                    .build();
        }
    }
}
