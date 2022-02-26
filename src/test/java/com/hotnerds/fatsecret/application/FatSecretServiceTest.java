package com.hotnerds.fatsecret.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(value = FatSecretService.class)
class FatSecretServiceTest {

    private final FatSecretService fatSecretService;

    private final MockRestServiceServer mockRestServiceServer;

    private final String API_URI_PREFIX =  "https://platform.fatsecret.com/rest/server.api";

    @Autowired
    public FatSecretServiceTest(FatSecretService fatSecretService,
        MockRestServiceServer mockRestServiceServer) {
        this.fatSecretService = fatSecretService;
        this.mockRestServiceServer = mockRestServiceServer;
    }

}