package com.hotnerds.food.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.fatsecret.application.FatSecretApiClient;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    FatSecretApiClient apiClient;

    @Mock
    FoodRepository foodRepository;

    @InjectMocks
    FoodService foodService;

    @Test
    @DisplayName("존재하지 않는 음식은 조회할 수 없다.")
    void 존재하지_않는_음식_조회시_실패() {
        //given
        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> foodService.findById(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.FOOD_NOT_FOUND_EXCEPTION.getMessage());
        verify(foodRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("음식을 조회할 수 있다.")
    void 음식_조회() {
        //given
        Food expectedFood = Food.builder()
                .foodId(123L)
                .foodName("치킨")
                .nutrient(Nutrient.builder()
                        .calories(1.0)
                        .carbs(1.0)
                        .protein(1.0)
                        .fat(1.0)
                        .build())
                .build();

        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(expectedFood));

        //when
        Food actualFood = foodService.findById(123L);

        //then
        assertThat(actualFood).isEqualTo(expectedFood);
        verify(foodRepository, times(1)).findById(123L);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("API id에 해당하는 음식이 없으면 외부 API로 정보를 가져온 후 음식을 생성한다.")
    void 음식이_없으면_생성() throws JsonProcessingException {
        //given
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = "{ \"food\": {\"food_id\": \"1641\", \"food_name\": \"Chicken Breast\", \"food_type\": \"Generic\", \"food_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten\", \"servings\": { \"serving\": [ {\"calcium\": \"12\", \"calories\": \"164\", \"carbohydrate\": \"0\", \"cholesterol\": \"70\", \"fat\": \"6.48\", \"fiber\": \"0\", \"iron\": \"0.89\", \"measurement_description\": \"small breast (yield after cooking, bone removed)\", \"metric_serving_amount\": \"84.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"2.524\", \"number_of_units\": \"0.500\", \"polyunsaturated_fat\": \"1.383\", \"potassium\": \"204\", \"protein\": \"24.82\", \"saturated_fat\": \"1.824\", \"serving_description\": \"1\\/2 small (yield after cooking, bone removed)\", \"serving_id\": \"5034\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5034&portionamount=0.500\", \"sodium\": \"330\", \"sugar\": \"0\", \"vitamin_a\": \"24\", \"vitamin_c\": \"0\" }, {\"calcium\": \"14\", \"calories\": \"191\", \"carbohydrate\": \"0\", \"cholesterol\": \"81\", \"fat\": \"7.57\", \"fiber\": \"0\", \"iron\": \"1.04\", \"measurement_description\": \"medium breast (yield after cooking, bone removed)\", \"metric_serving_amount\": \"98.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"2.945\", \"number_of_units\": \"0.500\", \"polyunsaturated_fat\": \"1.613\", \"potassium\": \"238\", \"protein\": \"28.96\", \"saturated_fat\": \"2.128\", \"serving_description\": \"1\\/2 medium (yield after cooking, bone removed)\", \"serving_id\": \"5035\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5035&portionamount=0.500\", \"sodium\": \"385\", \"sugar\": \"0\", \"vitamin_a\": \"27\", \"vitamin_c\": \"0\" }, {\"calcium\": \"16\", \"calories\": \"216\", \"carbohydrate\": \"0\", \"cholesterol\": \"92\", \"fat\": \"8.57\", \"fiber\": \"0\", \"iron\": \"1.18\", \"measurement_description\": \"large breast (yield after cooking, bone removed)\", \"metric_serving_amount\": \"111.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"3.335\", \"number_of_units\": \"0.500\", \"polyunsaturated_fat\": \"1.827\", \"potassium\": \"270\", \"protein\": \"32.80\", \"saturated_fat\": \"2.411\", \"serving_description\": \"1\\/2 large (yield after cooking, bone removed)\", \"serving_id\": \"5036\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5036&portionamount=0.500\", \"sodium\": \"436\", \"sugar\": \"0\", \"vitamin_a\": \"31\", \"vitamin_c\": \"0\" }, {\"calcium\": \"22\", \"calories\": \"302\", \"carbohydrate\": \"0\", \"cholesterol\": \"129\", \"fat\": \"11.97\", \"fiber\": \"0\", \"iron\": \"1.64\", \"measurement_description\": \"breast quarter (yield after cooking, bone removed)\", \"metric_serving_amount\": \"155.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"4.657\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"2.552\", \"potassium\": \"377\", \"protein\": \"45.80\", \"saturated_fat\": \"3.366\", \"serving_description\": \"1 breast quarter (yield after cooking, bone removed)\", \"serving_id\": \"4831\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=4831&portionamount=1.000\", \"sodium\": \"609\", \"sugar\": \"0\", \"vitamin_a\": \"43\", \"vitamin_c\": \"0\" }, {\"calcium\": \"1\", \"calories\": \"14\", \"carbohydrate\": \"0\", \"cholesterol\": \"6\", \"fat\": \"0.54\", \"fiber\": \"0\", \"iron\": \"0.07\", \"measurement_description\": \"thin slice (approx 2\\\" x 1-1\\/2\\\" x 1\\/8\\\")\", \"metric_serving_amount\": \"7.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.210\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.115\", \"potassium\": \"17\", \"protein\": \"2.07\", \"saturated_fat\": \"0.152\", \"serving_description\": \"1 thin slice (approx 2\\\" x 1-1\\/2\\\" x 1\\/8\\\")\", \"serving_id\": \"4832\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=4832&portionamount=1.000\", \"sodium\": \"28\", \"sugar\": \"0\", \"vitamin_a\": \"2\", \"vitamin_c\": \"0\" }, {\"calcium\": \"2\", \"calories\": \"27\", \"carbohydrate\": \"0\", \"cholesterol\": \"12\", \"fat\": \"1.08\", \"fiber\": \"0\", \"iron\": \"0.15\", \"measurement_description\": \"medium slice (approx 2\\\" x 1-1\\/2\\\" x 1\\/4\\\")\", \"metric_serving_amount\": \"14.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.421\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.230\", \"potassium\": \"34\", \"protein\": \"4.14\", \"saturated_fat\": \"0.304\", \"serving_description\": \"1 medium slice (approx 2\\\" x 1-1\\/2\\\" x 1\\/4\\\")\", \"serving_id\": \"5037\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5037&portionamount=1.000\", \"sodium\": \"55\", \"sugar\": \"0\", \"vitamin_a\": \"4\", \"vitamin_c\": \"0\" }, {\"calcium\": \"3\", \"calories\": \"41\", \"carbohydrate\": \"0\", \"cholesterol\": \"17\", \"fat\": \"1.62\", \"fiber\": \"0\", \"iron\": \"0.22\", \"measurement_description\": \"thick slice (approx 2\\\" x 1-1\\/2\\\" x 3\\/8\\\")\", \"metric_serving_amount\": \"21.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.631\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.346\", \"potassium\": \"51\", \"protein\": \"6.21\", \"saturated_fat\": \"0.456\", \"serving_description\": \"1 thick slice (approx 2\\\" x 1-1\\/2\\\" x 3\\/8\\\")\", \"serving_id\": \"5038\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5038&portionamount=1.000\", \"sodium\": \"83\", \"sugar\": \"0\", \"vitamin_a\": \"6\", \"vitamin_c\": \"0\" }, {\"calcium\": \"3\", \"calories\": \"47\", \"carbohydrate\": \"0\", \"cholesterol\": \"20\", \"fat\": \"1.85\", \"fiber\": \"0\", \"iron\": \"0.25\", \"measurement_description\": \"oz, with bone, cooked (yield after bone removed)\", \"metric_serving_amount\": \"24.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.721\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.395\", \"potassium\": \"58\", \"protein\": \"7.09\", \"saturated_fat\": \"0.521\", \"serving_description\": \"1 oz, with bone cooked (yield after bone removed)\", \"serving_id\": \"5039\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5039&portionamount=1.000\", \"sodium\": \"94\", \"sugar\": \"0\", \"vitamin_a\": \"7\", \"vitamin_c\": \"0\" }, {\"calcium\": \"2\", \"calories\": \"29\", \"carbohydrate\": \"0\", \"cholesterol\": \"12\", \"fat\": \"1.16\", \"fiber\": \"0\", \"iron\": \"0.16\", \"measurement_description\": \"oz, raw (yield after cooking, bone removed)\", \"metric_serving_amount\": \"15.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.451\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.247\", \"potassium\": \"36\", \"protein\": \"4.43\", \"saturated_fat\": \"0.326\", \"serving_description\": \"1 oz raw (yield after cooking, bone removed)\", \"serving_id\": \"5040\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5040&portionamount=1.000\", \"sodium\": \"59\", \"sugar\": \"0\", \"vitamin_a\": \"4\", \"vitamin_c\": \"0\" }, {\"calcium\": \"4\", \"calories\": \"55\", \"carbohydrate\": \"0\", \"cholesterol\": \"24\", \"fat\": \"2.19\", \"fiber\": \"0\", \"iron\": \"0.30\", \"measurement_description\": \"oz, boneless, cooked\", \"metric_serving_amount\": \"28.350\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.852\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.467\", \"potassium\": \"69\", \"protein\": \"8.38\", \"saturated_fat\": \"0.616\", \"serving_description\": \"1 oz boneless, cooked\", \"serving_id\": \"4833\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=4833&portionamount=1.000\", \"sodium\": \"111\", \"sugar\": \"0\", \"vitamin_a\": \"8\", \"vitamin_c\": \"0\" }, {\"calcium\": \"3\", \"calories\": \"35\", \"carbohydrate\": \"0\", \"cholesterol\": \"15\", \"fat\": \"1.39\", \"fiber\": \"0\", \"iron\": \"0.19\", \"measurement_description\": \"oz, boneless, raw (yield after cooking)\", \"metric_serving_amount\": \"18.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.541\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.296\", \"potassium\": \"44\", \"protein\": \"5.32\", \"saturated_fat\": \"0.391\", \"serving_description\": \"1 oz boneless (yield after cooking)\", \"serving_id\": \"5041\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5041&portionamount=1.000\", \"sodium\": \"71\", \"sugar\": \"0\", \"vitamin_a\": \"5\", \"vitamin_c\": \"0\" }, {\"calcium\": \"2\", \"calories\": \"33\", \"carbohydrate\": \"0\", \"cholesterol\": \"14\", \"fat\": \"1.31\", \"fiber\": \"0\", \"iron\": \"0.18\", \"measurement_description\": \"cubic inch, boneless, cooked\", \"metric_serving_amount\": \"17.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"0.511\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"0.280\", \"potassium\": \"41\", \"protein\": \"5.02\", \"saturated_fat\": \"0.369\", \"serving_description\": \"1 cubic inch boneless cooked\", \"serving_id\": \"5042\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5042&portionamount=1.000\", \"sodium\": \"67\", \"sugar\": \"0\", \"vitamin_a\": \"5\", \"vitamin_c\": \"0\" }, {\"calcium\": \"19\", \"calories\": \"263\", \"carbohydrate\": \"0\", \"cholesterol\": \"112\", \"fat\": \"10.42\", \"fiber\": \"0\", \"iron\": \"1.43\", \"measurement_description\": \"cup, cooked, diced\", \"metric_serving_amount\": \"135.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"4.056\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"2.222\", \"potassium\": \"328\", \"protein\": \"39.89\", \"saturated_fat\": \"2.932\", \"serving_description\": \"1 cup cooked, diced\", \"serving_id\": \"4834\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=4834&portionamount=1.000\", \"sodium\": \"531\", \"sugar\": \"0\", \"vitamin_a\": \"38\", \"vitamin_c\": \"0\" }, {\"calcium\": \"14\", \"calories\": \"191\", \"carbohydrate\": \"0\", \"cholesterol\": \"81\", \"fat\": \"7.57\", \"fiber\": \"0\", \"iron\": \"1.04\", \"measurement_description\": \"serving (98g)\", \"metric_serving_amount\": \"98.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"2.945\", \"number_of_units\": \"1.000\", \"polyunsaturated_fat\": \"1.613\", \"potassium\": \"238\", \"protein\": \"28.96\", \"saturated_fat\": \"2.128\", \"serving_description\": \"1 serving (98 g)\", \"serving_id\": \"5043\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=5043&portionamount=1.000\", \"sodium\": \"385\", \"sugar\": \"0\", \"vitamin_a\": \"27\", \"vitamin_c\": \"0\" }, {\"calcium\": \"14\", \"calories\": \"195\", \"carbohydrate\": \"0\", \"cholesterol\": \"83\", \"fat\": \"7.72\", \"fiber\": \"0\", \"iron\": \"1.06\", \"measurement_description\": \"g\", \"metric_serving_amount\": \"100.000\", \"metric_serving_unit\": \"g\", \"monounsaturated_fat\": \"3.005\", \"number_of_units\": \"100.000\", \"polyunsaturated_fat\": \"1.646\", \"potassium\": \"243\", \"protein\": \"29.55\", \"saturated_fat\": \"2.172\", \"serving_description\": \"100 g\", \"serving_id\": \"50321\", \"serving_url\": \"https:\\/\\/www.fatsecret.com\\/calories-nutrition\\/generic\\/chicken-breast-ns-as-to-skin-eaten?portionid=50321&portionamount=100.000\", \"sodium\": \"393\", \"sugar\": \"0\", \"vitamin_a\": \"28\", \"vitamin_c\": \"0\" } ] } }}";
        Map<String, Object> apiResponse = objectMapper.readValue(jsonResponse, Map.class);

        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(foodRepository.save(any(Food.class))).thenAnswer((Answer<Food>) invocation -> invocation.getArgument(0));
        when(apiClient.searchFoodById(anyLong())).thenReturn(ResponseEntity.ok(apiResponse));

        //when
        Food food = foodService.findOrCreate(1641L);

        //then
        assertThat(food.getFoodId()).isEqualTo(1641L);
        assertThat(food.getFoodName()).isEqualTo("Chicken Breast");
        assertThat(food.getNutrient().getCalories()).isEqualTo(164);
        assertThat(food.getNutrient().getCarbs()).isEqualTo(0);
        assertThat(food.getNutrient().getProtein()).isEqualTo(24.82);
        assertThat(food.getNutrient().getFat()).isEqualTo(6.48);
        verify(foodRepository, times(1)).findById(1641L);
        verify(apiClient, times(1)).searchFoodById(1641L);
    }

    @Test
    @DisplayName("API id에 해당하는 음식이 존재하면 조회한다.")
    void 음식이_존재하면_조회() {
        //given
        Food expectedFood = Food.builder()
                .foodId(1641L)
                .foodName("Chicken Breast")
                .nutrient(Nutrient.builder()
                        .calories(164.0)
                        .carbs(0.0)
                        .protein(24.82)
                        .fat(6.48)
                        .build())
                .build();

        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(expectedFood));
        when(foodRepository.save(any(Food.class))).thenAnswer((Answer<Food>) invocation -> invocation.getArgument(0));

        //when
        Food actualFood = foodService.findOrCreate(1641L);

        //then
        assertThat(actualFood).isEqualTo(expectedFood);
        verify(foodRepository, times(1)).findById(1641L);
        verify(foodRepository, times(1)).save(expectedFood);
        verify(apiClient, times(0)).searchFoodById(1641L);

    }
}