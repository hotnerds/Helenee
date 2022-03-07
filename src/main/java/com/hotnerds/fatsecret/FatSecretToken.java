package com.hotnerds.fatsecret;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.FatSecretConfig;
import com.hotnerds.fatsecret.exception.FatSecretResponseErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FatSecretToken {

    private final String API_URI_PREFIX = "https://oauth.fatsecret.com/connect/token";

    private final String GRANT_TYPE = "grant_type";

    private final String SCOPE = "scope";

    private final String CLIENT_CREDENTIALS = "client_credentials";

    private final String BASIC_SCOPE = "basic";

    private final String ACCESS_TOKEN = "access_token";

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final FatSecretConfig fatSecretConfig;

    private final RestTemplate restTemplate;

    private String accessToken;

    @Autowired
    public FatSecretToken(RestTemplateBuilder restTemplateBuilder, FatSecretConfig fatSecretConfig, ObjectMapper objectMapper) {
        this.fatSecretConfig = fatSecretConfig;
        this.restTemplate = restTemplateBuilder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .errorHandler(new FatSecretResponseErrorHandler(objectMapper))
                .build();
    }

    @PostConstruct
    public void initToken() {
        updateToken();
    }

    public void updateToken() {
        try {
            accessToken = requestAccessToken().getBody()
                    .get(ACCESS_TOKEN)
                    .toString();
        } catch(RestClientResponseException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<Map<String, Object>> requestAccessToken() throws RestClientResponseException {
        URI url = UriComponentsBuilder
                .fromHttpUrl(API_URI_PREFIX)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(fatSecretConfig.getId(), fatSecretConfig.getSecret());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE, CLIENT_CREDENTIALS);
        params.add(SCOPE, BASIC_SCOPE);

        RequestEntity<MultiValueMap<String, String>> request = RequestEntity.post(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(headers)
                .body(params);

        return restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
    }

    public String getToken() {
        return this.accessToken;
    }


}
