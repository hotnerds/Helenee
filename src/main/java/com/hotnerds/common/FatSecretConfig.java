package com.hotnerds.common;

import static com.hotnerds.common.FatSecretConfig.FATSECRET_CONFIG_PREFIX;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConfigurationProperties(prefix = FATSECRET_CONFIG_PREFIX)
@Getter
@Setter
public class FatSecretConfig {

    public static final String FATSECRET_CONFIG_PREFIX = "fat-secret";

    private String token;
    private String id;
    private String secret;
    private String test;
}
