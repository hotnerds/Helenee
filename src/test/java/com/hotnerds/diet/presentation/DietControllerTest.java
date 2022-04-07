package com.hotnerds.diet.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.diet.application.DietService;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.diet.domain.dto.DietReadRequestDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.dto.MealTimeDto;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DietController.class)
class DietControllerTest extends ControllerTest {

    @MockBean
    DietService dietService;

    LocalDate mealDate = LocalDate.of(2022, 4, 5);

    MealTime mealTime = MealTime.BREAKFAST;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .username("hi")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    }

    @Test
    @WithCustomMockUser
    void 날짜_시간으로_식단_조회() throws Exception {
        //given
        FoodResponseDto food1 = FoodResponseDto.builder()
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
        FoodResponseDto food2 = FoodResponseDto.builder()
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
        DietResponseDto responseDto = DietResponseDto.builder()
                .dietId(1L)
                .mealDate(mealDate)
                .mealTime(MealTimeDto.of(mealTime))
                .foodList(List.of(food1, food2))
                .totalNutrient(
                        food1.getNutrient().plus(food2.getNutrient())
                )
                .build();

        when(dietService.find(anyLong()))
                .thenReturn(responseDto);

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
                                fieldWithPath("dietId").description("식단 ID"),
                                fieldWithPath("mealDate").description("식단 날짜"),
                                fieldWithPath("mealTime.key").description("식사 시간 코드"),
                                fieldWithPath("mealTime.value").description("식사 시간 코드 이름"),
                                subsectionWithPath("totalNutrient").description("식단 음식들의 총 영양 성분"),
                                fieldWithPath("foodList[].foodId").description("음식 아이디"),
                                fieldWithPath("foodList[].foodName").description("음식 이름"),
                                subsectionWithPath("foodList[].nutrient").description("음식의 영양 성분")
                        )
                ));
    }
}