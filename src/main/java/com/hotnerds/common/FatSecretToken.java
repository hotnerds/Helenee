package com.hotnerds.common;

import com.hotnerds.fatsecret.application.FatSecretProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FatSecretToken {

    private final FatSecretProvider fatSecretProvider;

    private String accessToken;

    @PostConstruct
    public void initToken() {
        updateToken();
    }

    public void updateToken() {
        final ResponseEntity<Map<String, Object>> tokenGetResponse = fatSecretProvider.getAccessToken();
        accessToken = Objects.requireNonNull(tokenGetResponse.getBody())
                .get("access_token")
                .toString();
    }

    public String getToken() {
        return this.accessToken;
    }


}
