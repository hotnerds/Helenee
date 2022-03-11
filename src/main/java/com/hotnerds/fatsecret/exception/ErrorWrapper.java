package com.hotnerds.fatsecret.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorWrapper {

    private FatSecretResponseError error;
}
