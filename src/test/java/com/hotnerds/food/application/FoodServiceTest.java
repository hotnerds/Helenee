package com.hotnerds.food.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import com.hotnerds.food.domain.repository.FoodRepository;
import com.hotnerds.food.domain.apiclient.FoodApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    FoodApiClient apiClient;

    @Mock
    FoodRepository foodRepository;

    @InjectMocks
    FoodService foodService;

    Food food;

    @BeforeEach
    void setUp() {
        food = Food.builder()
                .foodId(1641L)
                .foodName("Chicken Breast")
                .nutrient(new Nutrient(164.0, 0.0, 24.82, 6.48))
                .build();
    }

    @Test
    @DisplayName("해당하는 음식이 없으면 외부 API로 정보를 가져온 후 음식을 생성한다.")
    void 음식이_없으면_생성() throws JsonProcessingException {
        //given
        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(foodRepository.save(any(Food.class))).thenAnswer((Answer<Food>) invocation -> invocation.getArgument(0));
        when(apiClient.searchFoodById(anyLong())).thenReturn(food);

        //when
        Food actualFood = foodService.findOrCreate(1641L);

        //then
        assertThat(actualFood).isEqualTo(food);
        verify(foodRepository, times(1)).findById(1641L);
        verify(apiClient, times(1)).searchFoodById(1641L);
    }

    @Test
    @DisplayName("해당하는 음식이 존재하면 조회한다.")
    void 음식이_존재하면_조회() {
        //given
        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
        when(foodRepository.save(any(Food.class))).thenAnswer((Answer<Food>) invocation -> invocation.getArgument(0));

        //when
        Food actualFood = foodService.findOrCreate(1641L);

        //then
        assertThat(actualFood).isEqualTo(food);
        verify(foodRepository, times(1)).findById(1641L);
        verify(apiClient, times(0)).searchFoodById(1641L);

    }
}