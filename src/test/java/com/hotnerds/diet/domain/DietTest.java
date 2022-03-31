package com.hotnerds.diet.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.food.domain.Food;
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
}