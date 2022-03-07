package com.hotnerds.common;

import static com.hotnerds.common.FatSecretConfig.FAT_SECRET_CONFIG_PREFIX;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConfigurationProperties(prefix = FAT_SECRET_CONFIG_PREFIX)
@Getter
public class FatSecretConfig {

    public static final String FAT_SECRET_CONFIG_PREFIX = "fat-secret";

    private String id;
    private String secret;
}
