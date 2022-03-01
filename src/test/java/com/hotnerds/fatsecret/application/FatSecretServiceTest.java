package com.hotnerds.fatsecret.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hotnerds.fatsecret.domain.dto.FatSecretDetailResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(value = FatSecretService.class)
class FatSecretServiceTest {

    private final FatSecretService fatSecretService;

    private final MockRestServiceServer mockServer;

    private final String API_URI_PREFIX = "https://platform.fatsecret.com/rest/server.api?";

    @Autowired
    public FatSecretServiceTest(FatSecretService fatSecretService,
        MockRestServiceServer mockServer) {
        this.fatSecretService = fatSecretService;
        this.mockServer = mockServer;
    }

    @Test
    @DisplayName("foodId를 통해 해당 food의 정보를 가져오거나 상황에 맞는 에러를 발생시킨다.")
    public void getFoodById() {
        // given
        Long foodId = 1114L;
        String exampleFoodIdQuery = "food_id=" + foodId.toString();
        String exampleFormatQuery = "format=json";
        String exampleMethodQuery = "method=food.get.v2";

        String exampleRequestURI =
            API_URI_PREFIX + exampleFoodIdQuery + "&" + exampleFormatQuery + "&"
                + exampleMethodQuery;

        String expectedResponse = "{\"foodApiId\": 1114, \"name\": \"Chocolate Soft Serve Ice Cream\",\"calories\": 355.0,\"carbs\": 48.22, \"protein\": 6.40,\"fat\": 16.73}";

        mockServer.expect(requestTo(exampleRequestURI))
            .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // when
        FatSecretDetailResponseDto response = fatSecretService.getFoodById(foodId);

        // then
        assertThat(response).isEqualTo(expectedResponse);
    }
}