package com.hotnerds.fatsecret.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class ServingWrapper {

    @JsonProperty("serving")
    private List<FatSecretServing> fatSecretServingList;
}
