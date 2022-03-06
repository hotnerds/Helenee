package com.hotnerds.fatsecret.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FatSecretFood {

    @JsonProperty("food_id")
    private Long foodId;

    @JsonProperty("food_name")
    private String foodName;

    @JsonProperty("servings")
    private ServingWrapper servingWrapper;
}
