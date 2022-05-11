package com.hotnerds.unit.diet.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class DietTest {

    Diet diet;

    public DietTest() {
        diet = Diet.builder()
                .build();
    }

    @Test
    @DisplayName("식단에 음식을 추가할 수 있다.")
    void 식단에_음식_추가() {

        //given
        Food food1 = Food.builder()
                .foodName("치킨")
                .build();
        Food food2 = Food.builder()
                .foodName("햄버거")
                .build();

        //when
        diet.addFood(food1, 1L);
        diet.addFood(food2, 1L);

        //then
        assertThat(diet.getFoods().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("식단에 포함 된 음식들의 영양 성분의 총합을 계산한다.")
    void 식단에_포함된_음식의_영양성분_총합_계산() {

        //given
        Food food1 = Food.builder()
                .foodName("치킨")
                .nutrient(new Nutrient(1.0, 1.0, 1.0, 1.0))
                .build();
        Food food2 = Food.builder()
                .foodName("햄버거")
                .nutrient(new Nutrient(2.0, 2.0, 2.0, 2.0))
                .build();

        diet.addFood(food1, 1L);
        diet.addFood(food2, 2L);

        //when
        Nutrient totalNutrient = diet.calculateTotalNutrient();

        //then
        assertAll(
                () -> assertThat(totalNutrient.getCalories()).isEqualTo(1.0 + 2.0 * 2),
                () -> assertThat(totalNutrient.getCarbs()).isEqualTo(1.0 + 2.0 * 2),
                () -> assertThat(totalNutrient.getProtein()).isEqualTo(1.0 + 2.0 * 2),
                () -> assertThat(totalNutrient.getFat()).isEqualTo(1.0 + 2.0 * 2)
        );
    }
}