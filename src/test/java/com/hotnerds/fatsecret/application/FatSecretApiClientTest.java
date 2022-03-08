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
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
        assertThrows(FatSecretResponseErrorException.class, () -> fatSecretApiClient.searchFoodById(foodId));
    }

    @Test
    @DisplayName("음식 id로 음식 정보 조회를 하여 음식 이름, 칼로리, 탄수화물, 단백질, 지방 정보를 찾을 수 있다.")
    void 음식_정보_조회() throws IOException {
        //given
        Long foodId = 38821L;
        String jsonResponse = "{ \"food\": {\"food_id\": \"38821\", \"food_name\": \"Toasted White Bread\", \"food_type\": \"Generic\", \"food_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread\", \"servings\": { \"serving\": [ {\"calcium\": \"54\", \"calories\": \"132\", \"carbohydrate\": \"24.48\", \"cholesterol\": \"0\", \"fat\": \"1.80\", \"fiber\": \"1.1\", \"iron\": \"1.50\", \"measurement_description\": \"cup, crumbs\", \"metric_serving_amount\": \"45.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.358\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.940\", \"potassium\": \"59\", \"protein\": \"4.05\", \"saturated_fat\": \"0.260\", \"serving_description\": \"1 cup crumbs\", \"serving_id\": \"38638\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38638&portionamount=1.000\", \"sodium\": \"266\", \"sugar\": \"2.13\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"50\", \"calories\": \"123\", \"carbohydrate\": \"22.85\", \"cholesterol\": \"0\", \"fat\": \"1.68\", \"fiber\": \"1.0\", \"iron\": \"1.40\", \"measurement_description\": \"cup, cubes\", \"metric_serving_amount\": \"42.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.334\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.878\", \"potassium\": \"55\", \"protein\": \"3.78\", \"saturated_fat\": \"0.243\", \"serving_description\": \"1 cup cubed\", \"serving_id\": \"38639\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38639&portionamount=1.000\", \"sodium\": \"249\", \"sugar\": \"1.99\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"34\", \"calories\": \"83\", \"carbohydrate\": \"15.42\", \"cholesterol\": \"0\", \"fat\": \"1.13\", \"fiber\": \"0.7\", \"iron\": \"0.94\", \"measurement_description\": \"oz\", \"metric_serving_amount\": \"28.350\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.226\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.593\", \"potassium\": \"37\", \"protein\": \"2.55\", \"saturated_fat\": \"0.164\", \"serving_description\": \"1 oz\", \"serving_id\": \"38640\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38640&portionamount=1.000\", \"sodium\": \"168\", \"sugar\": \"1.34\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"32\", \"calories\": \"79\", \"carbohydrate\": \"14.69\", \"cholesterol\": \"0\", \"fat\": \"1.08\", \"fiber\": \"0.7\", \"iron\": \"0.90\", \"measurement_description\": \"slice, large\", \"metric_serving_amount\": \"27.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.215\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.564\", \"potassium\": \"35\", \"protein\": \"2.43\", \"saturated_fat\": \"0.156\", \"serving_description\": \"1 slice large\", \"serving_id\": \"38641\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38641&portionamount=1.000\", \"sodium\": \"160\", \"sugar\": \"1.28\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"13\", \"calories\": \"32\", \"carbohydrate\": \"5.98\", \"cholesterol\": \"0\", \"fat\": \"0.44\", \"fiber\": \"0.3\", \"iron\": \"0.37\", \"measurement_description\": \"slice crust not eaten\", \"metric_serving_amount\": \"11.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.088\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.230\", \"potassium\": \"14\", \"protein\": \"0.99\", \"saturated_fat\": \"0.064\", \"serving_description\": \"1 slice crust not eaten\", \"serving_id\": \"38642\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38642&portionamount=1.000\", \"sodium\": \"65\", \"sugar\": \"0.52\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"20\", \"calories\": \"50\", \"carbohydrate\": \"9.25\", \"cholesterol\": \"0\", \"fat\": \"0.68\", \"fiber\": \"0.4\", \"iron\": \"0.57\", \"measurement_description\": \"slice, thin\", \"metric_serving_amount\": \"17.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.135\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.355\", \"potassium\": \"22\", \"protein\": \"1.53\", \"saturated_fat\": \"0.098\", \"serving_description\": \"1 slice, thin\", \"serving_id\": \"38643\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38643&portionamount=1.000\", \"sodium\": \"101\", \"sugar\": \"0.81\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"10\", \"calories\": \"23\", \"carbohydrate\": \"4.35\", \"cholesterol\": \"0\", \"fat\": \"0.32\", \"fiber\": \"0.2\", \"iron\": \"0.27\", \"measurement_description\": \"slice thin, crust not eaten\", \"metric_serving_amount\": \"8.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.064\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.167\", \"potassium\": \"10\", \"protein\": \"0.72\", \"saturated_fat\": \"0.046\", \"serving_description\": \"1 slice thin, crust not eaten\", \"serving_id\": \"38644\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38644&portionamount=1.000\", \"sodium\": \"47\", \"sugar\": \"0.38\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"15\", \"calories\": \"38\", \"carbohydrate\": \"7.07\", \"cholesterol\": \"0\", \"fat\": \"0.52\", \"fiber\": \"0.3\", \"iron\": \"0.43\", \"measurement_description\": \"slice, very thin\", \"metric_serving_amount\": \"13.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.103\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.272\", \"potassium\": \"17\", \"protein\": \"1.17\", \"saturated_fat\": \"0.075\", \"serving_description\": \"1 slice very thin\", \"serving_id\": \"38645\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38645&portionamount=1.000\", \"sodium\": \"77\", \"sugar\": \"0.62\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"26\", \"calories\": \"64\", \"carbohydrate\": \"11.97\", \"cholesterol\": \"0\", \"fat\": \"0.88\", \"fiber\": \"0.6\", \"iron\": \"0.73\", \"measurement_description\": \"slice\", \"metric_serving_amount\": \"22.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.175\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.460\", \"potassium\": \"29\", \"protein\": \"1.98\", \"saturated_fat\": \"0.127\", \"serving_description\": \"1 slice\", \"serving_id\": \"38646\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=38646&portionamount=1.000\", \"sodium\": \"130\", \"sugar\": \"1.04\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" }, {\"calcium\": \"119\", \"calories\": \"293\", \"carbohydrate\": \"54.40\", \"cholesterol\": \"1\", \"fat\": \"4.00\", \"fiber\": \"2.5\", \"iron\": \"3.33\", \"measurement_description\": \"g\", \"metric_serving_amount\": \"100.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.796\", \"number_of_units\": \"100.000\", \"polyunsaturated_fat\": \"2.090\", \"potassium\": \"131\", \"protein\": \"9.00\", \"saturated_fat\": \"0.578\", \"serving_description\": \"100 g\", \"serving_id\": \"61552\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/usda\\/toasted-white-bread?portionid=61552&portionamount=100.000\", \"sodium\": \"592\", \"sugar\": \"4.74\", \"vitamin_a\": \"0\", \"vitamin_c\": \"0.0\" } ] } }}";
        String requestURL = "https://platform.fatsecret.com/rest/server.api?method=food.get.v2&food_id=" + foodId + "&format=json";

        when(fatSecretResponseErrorHandler.hasError(any(ClientHttpResponse.class))).thenThrow(FatSecretResponseErrorException.class);
        mockServer.expect(requestTo(requestURL))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
        when(fatSecretResponseErrorHandler.hasError(any(ClientHttpResponse.class))).thenReturn(false);

        //when
        ResponseEntity<Map<String, Object>> foodResponse = fatSecretApiClient.searchFoodById(foodId);
        Map<String, Object> food = (Map<String, Object>) foodResponse.getBody().get("food");
        Map<String, Object> servings = (Map<String, Object>) food.get("servings");
        ArrayList<Map<String, String>> serving = (ArrayList<Map<String, String>>) servings.get("serving");

        //then
        assertThat(food.get("food_name").toString()).isEqualTo("Toasted White Bread");
        assertThat(serving.get(0).get("calories")).isEqualTo("132");
        assertThat(serving.get(0).get("carbohydrate")).isEqualTo("24.48");
        assertThat(serving.get(0).get("protein")).isEqualTo("4.05");
        assertThat(serving.get(0).get("fat")).isEqualTo("1.80");
    }

}