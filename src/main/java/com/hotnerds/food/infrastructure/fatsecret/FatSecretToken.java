package com.hotnerds.food.infrastructure.fatsecret;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Component
public class FatSecretToken {

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final String GRANT_TYPE = "grant_type";
    private final String SCOPE = "scope";
    private final String CLIENT_CREDENTIALS = "client_credentials";
    private final String BASIC_SCOPE = "basic";
    private final String ACCESS_TOKEN = "access_token";
    private final RestTemplate restTemplate;
    @Value("${fat-secret.token-request-url}")
    private String API_URI_PREFIX;
    @Value("${fat-secret.id}")
    private String ID;
    @Value("${fat-secret.secret}")
    private String SECRET;
    private String accessToken;

    @Autowired
    public FatSecretToken(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostConstruct
    public void initToken() {
        updateToken();
    }

    public void updateToken() {
        try {
            Map<String, Object> responseBody = Optional.ofNullable(requestAccessToken().getBody())
                    .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_COMMUNICATION_EXCEPTION));
            accessToken = responseBody
                    .get(ACCESS_TOKEN)
                    .toString();
        } catch (RestClientResponseException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<Map<String, Object>> requestAccessToken() throws RestClientResponseException {
        URI url = UriComponentsBuilder
                .fromHttpUrl(API_URI_PREFIX)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(ID, SECRET);

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
