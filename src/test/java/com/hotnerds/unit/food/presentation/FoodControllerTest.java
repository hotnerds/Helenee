package com.hotnerds.unit.food.presentation;

import com.hotnerds.unit.ControllerTest;
import com.hotnerds.utils.WithCustomMockUser;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.dto.FoodRequestByNameDto;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import com.hotnerds.food.presentation.FoodController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = FoodController.class)
class FoodControllerTest extends ControllerTest {

    @Test
    @WithCustomMockUser
    void 음식_리스트_검색() throws Exception {
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
        Mockito.when(foodService.searchFoods(any(FoodRequestByNameDto.class)))
                .thenReturn(List.of(food1, food2));

        //when
        ResultActions result = mockMvc.perform(
                get(FoodController.DEFAULT_URL)
                        .param("foodName", "Chicken")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("foods/search-foods",
                        requestParameters(
                                parameterWithName("foodName").description("음식 이름"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("음식 리스트"),
                                fieldWithPath("[].foodId").description("음식 아이디"),
                                fieldWithPath("[].foodName").description("음식 이름"),
                                subsectionWithPath("[].nutrient").description("음식의 영양 성분")
                        )
                ));
    }
}