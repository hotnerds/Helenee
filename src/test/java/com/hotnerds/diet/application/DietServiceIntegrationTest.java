package com.hotnerds.diet.application;

import com.hotnerds.IntegrationTest;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.diet.domain.dto.DietRequestByDateDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.dto.DietSaveFoodRequestDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.food.application.FoodService;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.food.domain.apiclient.FoodApiClient;
import com.hotnerds.food.domain.dto.FoodRequestDto;
import com.hotnerds.food.domain.repository.FoodRepository;
import com.hotnerds.food.infrastructure.fatsecret.FatSecretApiClient;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DietServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private DietService dietService;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FatSecretApiClient apiClient;

    @Autowired
    private FoodService foodService;

    @Autowired
    private FoodRepository foodRepository;

    User user;
    Food food;
    LocalDate mealDate;
    MealTime mealTime;
    FoodRequestDto foodRequestDto;

    @BeforeEach
    void setUp() {
        mealDate = LocalDate.parse("2022-03-20", DateTimeFormatter.ISO_DATE);

        mealTime = MealTime.BREAKFAST;

        user = new User("bae", "a@abc.com", ROLE.USER);

        food = Food.builder()
                .foodId(1641L)
                .foodName("Chicken Breast")
                .nutrient(new Nutrient(164.0, 0.0, 24.82, 6.48))
                .build();

        foodRequestDto = FoodRequestDto.builder()
                .foodId(1L)
                .amount(2L)
                .build();
    }

    @DisplayName("존재하지 않는 식단을 조회할 수 없다.")
    @Test
    void 존재하지_않는_식단_조회시_실패() {
        //given

        //when then
        assertThatThrownBy(
                () -> dietService.find(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DIET_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("아이디로 식단을 조회할 수 있다.")
    @Test
    void 아이디로_식단_조회() {
        //given
        userRepository.save(user);

        Diet expectedDiet = Diet.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .user(user)
                .build();
        dietRepository.save(expectedDiet);

        //when
        DietResponseDto actualDiet = dietService.find(1L);

        //then
        assertThat(actualDiet.getMealDate()).isEqualTo(mealDate);
        assertThat(actualDiet.getMealTime().getKey()).isEqualTo("BREAKFAST");
    }

    @DisplayName("날짜로 식단을 조회할 수 있다.")
    @Test
    void 날짜로_식단_조회() {
        //given
        userRepository.save(user);

        DietRequestByDateDto requestDto = DietRequestByDateDto.builder()
                .mealDate(mealDate)
                .build();

        Diet diet1 = Diet.builder()
                .mealDate(mealDate)
                .mealTime(MealTime.BREAKFAST)
                .user(user)
                .build();

        Diet diet2 = Diet.builder()
                .mealDate(mealDate)
                .mealTime(MealTime.LUNCH)
                .user(user)
                .build();

        dietRepository.save(diet1);
        dietRepository.save(diet2);

        //when
        List<DietResponseDto> diets = dietService.searchByDate(requestDto, 1L);

        //then
        assertThat(diets).hasSize(2);
    }

    @DisplayName("식단에 음식들을 추가한다.")
    @Test
    void 식단에_음식_저장() {
        //given
        userRepository.save(user);
        foodRepository.save(food);

        DietSaveFoodRequestDto requestDto = DietSaveFoodRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .foods(List.of(foodRequestDto))
                .build();

        //when
        dietService.saveFoods(requestDto, 1L);
        Diet diet = dietRepository.findByMealDateAndMealTimeAndUser(mealDate, mealTime, user).get();

        //then
        assertThat(diet.getFoods()).hasSize(1);
    }

    @DisplayName("기존 식단의 음식들을 변경한다.")
    @Test
    void 식단_음식_변경() {
        //given
        userRepository.save(user);
        foodRepository.save(food);

        Diet diet = Diet.builder()
                .mealDate(mealDate)
                .mealTime(MealTime.BREAKFAST)
                .user(user)
                .build();
        dietRepository.save(diet);

        DietSaveFoodRequestDto requestDto = DietSaveFoodRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .foods(List.of(foodRequestDto))
                .build();

        //when
        dietService.saveFoods(requestDto, 1L);

        //then
        assertThat(diet.getFoods()).hasSize(1);
    }
}
