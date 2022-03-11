package com.hotnerds.fatsecret;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.fatsecret.exception.FatSecretResponseErrorHandler;
import org.mockito.Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;

import static org.mockito.Mockito.mock;
@TestConfiguration
public class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }
    @Bean
    @DependsOn("simpleClientHttpRequestFactory")
    public BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory() {
        return new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory());
    }

    @Bean
    public MockServerRestTemplateCustomizer customizer() {
        return new MockServerRestTemplateCustomizer();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(FatSecretResponseErrorHandler errorHandler) {
        return new RestTemplateBuilder(customizer())
                .requestFactory(this::bufferingClientHttpRequestFactory)
                .errorHandler(errorHandler);
    }
}