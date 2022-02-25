package com.hotnerds.fatsecret.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class FatSecretServing {

    private Double calories;

    private Double carbohydrate;

    private Double fat;

    private Double protein;
}
