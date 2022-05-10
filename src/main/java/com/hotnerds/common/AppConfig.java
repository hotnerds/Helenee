package com.hotnerds.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.food.infrastructure.fatsecret.handler.FatSecretResponseErrorHandler;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);

        return modelMapper;
    }

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
    @DependsOn("objectMapper")
    public FatSecretResponseErrorHandler fatSecretResponseErrorHandler() {
        return new FatSecretResponseErrorHandler(objectMapper());
    }

    @Bean
    @DependsOn(value = {"bufferingClientHttpRequestFactory", "fatSecretResponseErrorHandler"})
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .requestFactory(this::bufferingClientHttpRequestFactory)
                .errorHandler(fatSecretResponseErrorHandler());
    }

    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(gracefulShutdown);
        return factory;
    }

}
