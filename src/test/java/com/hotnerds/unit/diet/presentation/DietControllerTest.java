package com.hotnerds.unit.diet.presentation;

import com.hotnerds.unit.ControllerTest;
import com.hotnerds.utils.WithCustomMockUser;
import com.hotnerds.diet.domain.dto.DietRequestByDateDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.dto.DietSaveFoodRequestDto;
import com.hotnerds.diet.domain.dto.MealTimeDto;
import com.hotnerds.diet.presentation.DietController;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.dto.FoodRequestDto;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DietController.class)
class DietControllerTest extends ControllerTest {

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
                .foodName("??????")
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
                .foodName("?????????")
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
                .foodName("??????")
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
                .foodName("????????????")
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
                fieldWithPath("dietId").description("?????? ID"),
                fieldWithPath("mealDate").description("?????? ??????"),
                fieldWithPath("mealTime.key").description("?????? ?????? ??????"),
                fieldWithPath("mealTime.value").description("?????? ?????? ?????? ??????"),
                subsectionWithPath("totalNutrient").description("?????? ???????????? ??? ?????? ??????"),
                fieldWithPath("foodList[].foodId").description("?????? ?????????"),
                fieldWithPath("foodList[].foodName").description("?????? ??????"),
                subsectionWithPath("foodList[].nutrient").description("????????? ?????? ??????")
        };

        User user = User.builder()
                .username("hi")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ????????? ????????? ????????????.")
    @WithCustomMockUser
    void ??????_????????????_??????_??????() throws Exception {
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
                                parameterWithName("dietId").description("?????? ID")
                        ),
                        responseFields(
                                dietFields
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ???????????? ????????????.")
    @WithCustomMockUser
    void ?????????_??????_??????() throws Exception {
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
                                parameterWithName("mealDate").description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("?????? ?????????")
                        ).andWithPrefix("[].", dietFields)
                ));
        verify(dietService, times(1))
                .searchByDate(any(DietRequestByDateDto.class), any());
    }

    @Test
    @DisplayName("?????? ?????? API")
    @WithCustomMockUser
    void ??????_??????_API() throws Exception {
        //given
        DietSaveFoodRequestDto requestDto = DietSaveFoodRequestDto.builder()
                .mealDate(LocalDate.of(2022, 4, 5))
                .mealTime(BREAKFAST)
                .foods(
                        List.of(new FoodRequestDto(1L, 2L), new FoodRequestDto(2L, 1L))
                )
                .build();
        when(dietService.saveFoods(any(DietSaveFoodRequestDto.class), any())).thenReturn(diet1.getDietId());

        //when
        ResultActions result = mockMvc.perform(post(DEFAULT_URL)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isCreated())
                .andDo(document("diets/save-foods",
                        requestFields(
                                fieldWithPath("mealDate").description("?????? ??????"),
                                fieldWithPath("mealTime").description("?????? ?????? ??????"),
                                fieldWithPath("foods[]").description("????????? ?????? ?????????"),
                                fieldWithPath("foods[].foodId").description("?????? ?????????"),
                                fieldWithPath("foods[].amount").description("?????????")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("????????? ????????? URL")
                        )
                ));
    }
}