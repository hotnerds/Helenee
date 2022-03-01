package com.hotnerds.fatsecret.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodWrapper {

    @JsonProperty("food")
    private FatSecretFood fatSecretFood;

    @JsonProperty("error")
    private FatSecretError fatSecretError;
}
