package com.hotnerds.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
class FatSecretToken {

    @JsonProperty("access_token")
    private String token;
}

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final FatSecretConfig fatSecretConfig;

    private final String API_URI_PREFIX = "https://oauth.fatsecret.com/connect/token";


    @Scheduled(cron = "* * */22 * * *")
    public void getTokenTask() {
        final String id = fatSecretConfig.getId();
        final String secret = fatSecretConfig.getSecret();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(id, secret);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("scope", "basic");

        HttpEntity httpEntity = new HttpEntity(params, headers);

        URI uri = UriComponentsBuilder
            .fromHttpUrl(API_URI_PREFIX)
            .build()
            .toUri();

        ResponseEntity<FatSecretToken> responseEntity = restTemplate.exchange(uri,
            HttpMethod.POST, httpEntity, FatSecretToken.class);

        FatSecretToken fatSecretToken = responseEntity.getBody();

        String accessToken = fatSecretToken.getToken();

        fatSecretConfig.setToken(accessToken);
    }
}
