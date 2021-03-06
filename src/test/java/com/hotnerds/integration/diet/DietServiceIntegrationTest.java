package com.hotnerds.integration.diet;

import com.hotnerds.integration.IntegrationTest;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.application.DietService;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.diet.domain.dto.DietRequestByDateDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.dto.DietSaveFoodRequestDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.food.application.FoodService;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DietServiceIntegrationTest extends IntegrationTest {

    User user;
    Food food;
    LocalDate mealDate;
    MealTime mealTime;
    FoodRequestDto foodRequestDto;
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

    @DisplayName("???????????? ?????? ????????? ????????? ??? ??????.")
    @Test
    void ????????????_??????_??????_?????????_??????() {
        //given

        //when then
        assertThatThrownBy(
                () -> dietService.find(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DIET_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("???????????? ????????? ????????? ??? ??????.")
    @Test
    void ????????????_??????_??????() {
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

    @DisplayName("????????? ????????? ????????? ??? ??????.")
    @Test
    void ?????????_??????_??????() {
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

    @DisplayName("????????? ???????????? ????????????.")
    @Test
    void ?????????_??????_??????() {
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

    @DisplayName("?????? ????????? ???????????? ????????????.")
    @Test
    void ??????_??????_??????() {
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
