package com.hotnerds.fatsecret.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hotnerds.common.FatSecretConfig;
import com.hotnerds.fatsecret.FatSecretToken;
import com.hotnerds.fatsecret.exception.FatSecretResponseErrorException;
import com.hotnerds.fatsecret.exception.FatSecretResponseErrorHandler;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@RestClientTest(value = FatSecretApiClient.class)
@MockBean({JpaMetamodelMappingContext.class, BufferingClientHttpRequestFactory.class})
class FatSecretApiClientTest {

    @MockBean
    FatSecretResponseErrorHandler fatSecretResponseErrorHandler;

    @MockBean
    FatSecretToken fatSecretToken;

    @Autowired
    FatSecretApiClient fatSecretApiClient;

    @Autowired
    MockRestServiceServer mockServer;


    @Test
    @DisplayName("유효하지 않은 음식 id는 정보를 찾을 수 없다.")
    void 유효하지_않은_음식_ID_음식_조회_실패() throws IOException {
        //given
        Long foodId = 1L;
        String jsonResponse = "{\n" +
                "    \"error\": {\n" +
                "        \"code\": 106,\n" +
                "        \"message\": \"Invalid ID: food_id '1' does not exist\"\n" +
                "    }\n" +
                "}";
        String requestURL = "https://platform.fatsecret.com/rest/server.api?method=food.get.v2&food_id=" + foodId + "&format=json";

        when(fatSecretResponseErrorHandler.hasError(any(ClientHttpResponse.class))).thenThrow(FatSecretResponseErrorException.class);
        mockServer.expect(requestTo(requestURL))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        //when then
        FatSecretResponseErrorException exception = assertThrows(FatSecretResponseErrorException.class, () -> fatSecretApiClient.searchFoodById(foodId));
    }
}