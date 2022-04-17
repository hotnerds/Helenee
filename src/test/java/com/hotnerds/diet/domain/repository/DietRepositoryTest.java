package com.hotnerds.diet.domain.repository;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.food.domain.repository.FoodRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DietRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DietRepository dietRepository;

    @Autowired
    FoodRepository foodRepository;

    LocalDate mealDate;

    MealTime mealTime;

    User user;

    @BeforeEach
    void setUp() {
        mealDate = LocalDate.parse("2022-03-21", DateTimeFormatter.ISO_DATE);
        mealTime = MealTime.BREAKFAST;
        user = User.builder()
                .username("name")
                .email("a@a.com")
                .build();
        userRepository.save(user);

    }

    @Test
    @DisplayName("식단을 생성할 수 있다.")
    void 식단_생성() {

        //given
        Diet diet = Diet.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .user(user)
                .build();

        //when
        Diet savedDiet = dietRepository.save(diet);

        //then
        assertThat(savedDiet.getId()).isNotNull();
        assertThat(savedDiet.getMealDate()).isEqualTo(mealDate);
        assertThat(savedDiet.getMealTime()).isEqualTo(mealTime);
        assertThat(savedDiet.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("식단의 날짜, 시간대, 유저로 식단을 조회할 수 있다.")
    void 식단_날짜_시간_유저로_식단_조회() {

        //given
        Diet diet = Diet.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .user(user)
                .build();

        dietRepository.save(diet);

        //when
        Diet actualDiet = dietRepository.findByMealDateAndMealTimeAndUser(mealDate, mealTime, user).get();

        //then
        assertThat(actualDiet).isEqualTo(diet);
    }

    @Test
    @DisplayName("식단의 날짜, 유저로 식단을 조회할 수 있다.")
    void 식단_날짜_유저로_식단_조회() {
        //given
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
        List<Diet> diets = dietRepository.findAllByMealDateAndUser(mealDate, user);

        //then
        assertThat(diets).hasSize(2);
    }
}