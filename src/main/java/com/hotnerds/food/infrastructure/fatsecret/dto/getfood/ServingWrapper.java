package com.hotnerds.food.infrastructure.fatsecret.dto.getfood;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServingWrapper {

    @JsonProperty("serving")
    List<Serving> servingList = new ArrayList<>();

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Serving {

        @JsonProperty("metric_serving_amount")
        private Double metricServingAmount;

        @JsonProperty("metric_serving_unit")
        private String metricServingUnit;

        @JsonProperty("calories")
        private Double calories;

        @JsonProperty("carbohydrate")
        private Double carbohydrate;

        @JsonProperty("protein")
        private Double protein;

        @JsonProperty("fat")
        private Double fat;

        @JsonProperty("saturated_fat")
        private Double saturatedFat;

        @JsonProperty("polyunsaturated_fat")
        private Double polyunsaturatedFat;

        @JsonProperty("monounsaturated_fat")
        private Double monounsaturatedFat;

        @JsonProperty("trans_fat")
        private Double transFat;

        @JsonProperty("cholesterol")
        private Double cholesterol;

        @JsonProperty("sodium")
        private Double sodium;

        @JsonProperty("potassium")
        private Double potassium;

        @JsonProperty("fiber")
        private Double fiber;

        @JsonProperty("sugar")
        private Double sugar;

        @JsonProperty("addedSugars")
        private Double addedSugars;

        @JsonProperty("vitamin_d")
        private Double vitaminD;

        @JsonProperty("vitamin_a")
        private Double vitaminA;

        @JsonProperty("vitamin_c")
        private Double vitaminC;

        @JsonProperty("calcium")
        private Double calcium;

        @JsonProperty("iron")
        private Double iron;
    }
}
