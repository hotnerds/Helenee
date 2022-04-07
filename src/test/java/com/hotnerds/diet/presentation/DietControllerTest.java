package com.hotnerds.diet.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.diet.application.DietService;
import com.hotnerds.diet.domain.dto.DietRequestByDateDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.dto.DietSaveFoodRequestDto;
import com.hotnerds.diet.domain.dto.MealTimeDto;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.dto.FoodRequestDto;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.hotnerds.diet.domain.MealTime.BREAKFAST;
import static com.hotnerds.diet.domain.MealTime.LUNCH;
import static com.hotnerds.diet.presentation.DietController.DEFAULT_URL;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DietController.class)
class DietControllerTest extends ControllerTest {

    @MockBean
    DietService dietService;

    LocalDate mealDate = LocalDate.of(2022, 4, 5);

    FoodResponseDto food1;

    FoodResponseDto food2;

    FoodResponseDto food3;

    FoodResponseDto food4;

    DietResponseDto diet1;

    DietResponseDto diet2;

    FieldDescriptor[] dietFields;

    @BeforeEach
    void setUp() {
        food1 = FoodResponseDto.builder()
                .foodId(1L)
                .foodName("치킨")
                .nutrient(
                        Nutrient.builder()
                                .calories(1000.0)
                                .carbs(100.0)
                                .protein(50.0)
                                .fat(50.0)
                                .build()
                )
                .build();
        food2 = FoodResponseDto.builder()
                .foodId(2L)
                .foodName("햄버거")
                .nutrient(
                        Nutrient.builder()
                                .calories(2000.0)
                                .carbs(100.0)
                                .protein(50.0)
                                .fat(50.0)
                                .build()
                )
                .build();
        food3 = FoodResponseDto.builder()
                .foodId(3L)
                .foodName("피자")
                .nutrient(
                        Nutrient.builder()
                                .calories(2000.0)
                                .carbs(100.0)
                                .protein(50.0)
                                .fat(50.0)
                                .build()
                )
                .build();
        food4 = FoodResponseDto.builder()
                .foodId(4L)
                .foodName("스파게티")
                .nutrient(
                        Nutrient.builder()
                                .calories(2000.0)
                                .carbs(100.0)
                                .protein(50.0)
                                .fat(50.0)
                                .build()
                )
                .build();
        diet1 = DietResponseDto.builder()
                .dietId(1L)
                .mealDate(mealDate)
                .mealTime(MealTimeDto.of(BREAKFAST))
                .foodList(List.of(food1, food2))
                .totalNutrient(
                        food1.getNutrient().plus(food2.getNutrient())
                )
                .build();
        diet2 = DietResponseDto.builder()
                .dietId(2L)
                .mealDate(mealDate)
                .mealTime(MealTimeDto.of(LUNCH))
                .foodList(List.of(food3, food4))
                .totalNutrient(
                        food3.getNutrient().plus(food4.getNutrient())
                )
                .build();

        dietFields = new FieldDescriptor[]{
                fieldWithPath("dietId").description("식단 ID"),
                fieldWithPath("mealDate").description("식단 날짜"),
                fieldWithPath("mealTime.key").description("식사 시간 코드"),
                fieldWithPath("mealTime.value").description("식사 시간 코드 이름"),
                subsectionWithPath("totalNutrient").description("식단 음식들의 총 영양 성분"),
                fieldWithPath("foodList[].foodId").description("음식 아이디"),
                fieldWithPath("foodList[].foodName").description("음식 이름"),
                subsectionWithPath("foodList[].nutrient").description("음식의 영양 성분")
        };

        User user = User.builder()
                .username("hi")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("식단 날짜와 식사 시간 코드로 식단을 조회한다.")
    @WithCustomMockUser
    void 날짜_시간으로_식단_조회() throws Exception {
        //given
        when(dietService.find(anyLong()))
                .thenReturn(diet1);

        //when
        ResultActions result = mockMvc.perform(
                get(DietController.DEFAULT_URL + "/{dietId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("diets/find-unit",
                        pathParameters(
                                parameterWithName("dietId").description("식단 ID")
                        ),
                        responseFields(
                                dietFields
                        )
                ));
    }

    @Test
    @DisplayName("식단 날짜로 식단 리스트를 조회한다.")
    @WithCustomMockUser
    void 날짜로_식단_조회() throws Exception {
        //given
        when(dietService.searchByDate(any(DietRequestByDateDto.class), any()))
                .thenReturn(
                        List.of(diet1, diet2)
                );

        //when
        ResultActions result = mockMvc.perform(
                get(DEFAULT_URL)
                        .param("mealDate", "2022-04-05")
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("diets/find-by-date",
                        requestParameters(
                                parameterWithName("mealDate").description("식단 날짜")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("식단 리스트")
                        ).andWithPrefix("[].", dietFields)
                ));
        verify(dietService, times(1))
                .searchByDate(any(DietRequestByDateDto.class), any());
    }

    @Test
    @DisplayName("음식 저장 API")
    @WithCustomMockUser
    void 음식_저장_API() throws Exception {
        //given
        DietSaveFoodRequestDto requestDto = DietSaveFoodRequestDto.builder()
                .mealDate(LocalDate.of(2022, 4, 5))
                .mealTime(BREAKFAST)
                .foods(
                        List.of(new FoodRequestDto(1L, 2L), new FoodRequestDto(2L, 1L))
                )
                .build();
        when(dietService.saveFoods(any(DietSaveFoodRequestDto.class), any())).thenReturn(diet1);

        //when
        ResultActions result = mockMvc.perform(post(DEFAULT_URL)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("diets/save-foods",
                        requestFields(
                                fieldWithPath("mealDate").description("식단 날짜"),
                                fieldWithPath("mealTime").description("식사 시간 코드"),
                                fieldWithPath("foods[]").description("저장할 음식 리스트"),
                                fieldWithPath("foods[].foodId").description("음식 아이디"),
                                fieldWithPath("foods[].amount").description("음식량")
                        ),
                        responseFields(
                                dietFields
                        )
                ));
    }
}