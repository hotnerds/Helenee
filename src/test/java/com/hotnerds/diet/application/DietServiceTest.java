package com.hotnerds.diet.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.domain.*;
import com.hotnerds.diet.domain.dto.DietRequestDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DietServiceTest {

    @Mock
    DietRepository dietRepository;

    @Mock
    UserService userService;

    @InjectMocks
    DietService dietService;

    @Test
    @DisplayName("식단 id가 존재하면 해당하는 Entity를, 존재하지 않으면 예외가 발생해야한다.")
    public void getDiet() {
        // given
        User user = User.builder()
                .username("zz")
                .email("zz@zz.com")
                .build();

        MealDateTime mealDateTime = MealDateTime.builder()
                .localDate(LocalDate.parse("2022-02-25", DateTimeFormatter.ISO_DATE))
                .mealTimeType(MealTimeType.BREAKFAST)
                .build();

        Nutrient nutrient = Nutrient.builder()
                .totalCalories(2000.0)
                .totalCarbs(100.0)
                .totalProtein(100.0)
                .totalFat(100.0)
                .build();

        Diet expectedDiet = Diet.builder()
                .user(user)
                .mealDateTime(mealDateTime)
                .nutrient(nutrient)
                .build();

        // when
        when(dietRepository.findById(1L)).thenReturn(Optional.of(expectedDiet));
        when(dietRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThat(dietService.getDietById(1L))
                .isEqualTo(expectedDiet);
        assertThatThrownBy(() -> dietService.getDietById(2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DIET_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("날짜, 식사 시간, 유저 정보가 같은 식단 Entity가 있으면 예외가 발생, 아니라면 정상적으로 저장")
    public void createDiet() {
        User user1 = User.builder()
                .username("zz")
                .email("zz@zz.com")
                .build();

        User user2 = User.builder()
                .username("bae")
                .email("bae@bae.com")
                .build();

        MealDateTime mealDateTime = MealDateTime.builder()
                .localDate(LocalDate.parse("2022-02-25", DateTimeFormatter.ISO_DATE))
                .mealTimeType(MealTimeType.BREAKFAST)
                .build();

        Nutrient nutrient = Nutrient.builder()
                .totalCalories(2000.0)
                .totalCarbs(100.0)
                .totalProtein(100.0)
                .totalFat(100.0)
                .build();

        Food food1 = Food.builder()
                .name("치킨")
                .nutrient(nutrient)
                .build();

        Food food2 = Food.builder()
                .name("햄버거")
                .nutrient(nutrient)
                .build();

        List<Food> foodList = List.of(food1, food2);

        DietRequestDto dto1 = DietRequestDto.builder()
                .mealDateTime(mealDateTime)
                .nutrient(nutrient)
                .userId(1L)
                .foodList(foodList)
                .build();

        DietRequestDto dto2 = DietRequestDto.builder()
                .mealDateTime(mealDateTime)
                .nutrient(nutrient)
                .userId(2L)
                .foodList(foodList)
                .build();

        Diet expectedDiet = Diet.builder()
                .mealDateTime(mealDateTime)
                .nutrient(nutrient)
                .user(user2)
                .build();

        // when
        when(userService.getUserById(1L)).thenReturn(user1);
        when(dietRepository.existsByMealDateTimeAndUser(mealDateTime, user1)).thenReturn(true);

        when(userService.getUserById(2L)).thenReturn(user2);
        when(dietRepository.existsByMealDateTimeAndUser(mealDateTime, user2)).thenReturn(false);
        when(dietRepository.save(any(Diet.class))).thenReturn(expectedDiet);

        // then
        assertThatThrownBy(() -> dietService.createDiet(dto1))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DIET_DUPLICATED_EXCEPTION.getMessage());
        assertThat(dietService.createDiet(dto2)).isEqualTo(expectedDiet);
    }
}