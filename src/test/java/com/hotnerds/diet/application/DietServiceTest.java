package com.hotnerds.diet.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.diet.domain.dto.DietAddFoodRequestDto;
import com.hotnerds.diet.domain.dto.DietReadRequestDto;
import com.hotnerds.diet.domain.dto.DietRemoveFoodRequestDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.food.application.FoodService;
import com.hotnerds.food.domain.Food;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DietServiceTest {

    @Mock
    FoodService foodService;

    @Mock
    DietRepository dietRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    DietService dietService;

    User user;
    Food food;
    LocalDate mealDate;
    MealTime mealTime;

    @BeforeEach
    void setUp() {
        mealDate = LocalDate.parse("2022-03-20", DateTimeFormatter.ISO_DATE);

        mealTime = MealTime.BREAKFAST;

        user = User.builder().build();

        food = Food.builder().build();
    }

    @Test
    @DisplayName("존재하지 않는 식단을 조회시 예외가 발생한다.")
    void 존재하지않는_식단_조회시_실패() {
        //given
        DietReadRequestDto requestDto = DietReadRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(dietRepository.findByMealDateAndMealTimeAndUser(requestDto.getMealDate(),
                requestDto.getMealTime(),
                user)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(
                () -> dietService.findByMealDateAndMealTimeAndUser(requestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DIET_NOT_FOUND_EXCEPTION.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(dietRepository, times(1)).findByMealDateAndMealTimeAndUser(requestDto.getMealDate(),
                requestDto.getMealTime(),
                user);
    }

    @Test
    @DisplayName("존재하지 않는 유저의 식단을 조회시 예외가 발생한다.")
    void 존재하지_않는_유저_식단_조회시_실패() {
        //given
        DietReadRequestDto requestDto = DietReadRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(
                () -> dietService.findByMealDateAndMealTimeAndUser(requestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(dietRepository, times(0)).findByMealDateAndMealTimeAndUser(requestDto.getMealDate(),
                requestDto.getMealTime(),
                user);
    }

    @Test
    @DisplayName("식단 정보를 조회한다.")
    void 식단_정보_조회() {
        //given
        DietReadRequestDto requestDto = DietReadRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .build();

        Diet expectedDiet = Diet.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .user(user)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(dietRepository.findByMealDateAndMealTimeAndUser(requestDto.getMealDate(),
                requestDto.getMealTime(),
                user)).thenReturn(Optional.of(expectedDiet));
      
        //when
        Diet actualDiet = dietService.findByMealDateAndMealTimeAndUser(requestDto, 1L);

        //then
        assertThat(actualDiet).isEqualTo(expectedDiet);
        verify(userRepository, times(1)).findById(1L);
        verify(dietRepository, times(1)).findByMealDateAndMealTimeAndUser(requestDto.getMealDate(),
                requestDto.getMealTime(),
                user);
    }

    @Test
    @DisplayName("식단이 존재하지 않으면 식단을 생성한다.")
    public void 식단이_존재하지_않으면_생성() {
        //given
        Diet expectedDiet = Diet.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .user(user)
                .build();

        when(dietRepository.findByMealDateAndMealTimeAndUser(mealDate, mealTime, user))
                .thenReturn(Optional.empty());

        when(dietRepository.save(any(Diet.class))).thenAnswer(new Answer<Diet>() {
            @Override
            public Diet answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        });



        //when
        Diet actualDiet = dietService.findOrCreate(mealDate, mealTime, user);

        //then
        assertThat(actualDiet).isEqualTo(expectedDiet);
        verify(dietRepository, times(1)).save(any(Diet.class));
        verify(dietRepository, times(1)).findByMealDateAndMealTimeAndUser(mealDate, mealTime, user);
    }

    @Test
    @DisplayName("식단이 이미 존재하면 가져온다.")
    public void 식단이_존재하면_가져옴() {
        //given
        Diet expectedDiet = Diet.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .user(user)
                .build();

        when(dietRepository.findByMealDateAndMealTimeAndUser(mealDate, mealTime, user))
                .thenReturn(Optional.of(expectedDiet));

        when(dietRepository.save(any(Diet.class))).thenAnswer(new Answer<Diet>() {
            @Override
            public Diet answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        });

        //when
        Diet actualDiet = dietService.findOrCreate(mealDate, mealTime, user);

        //then
        assertThat(actualDiet).isEqualTo(expectedDiet);
        verify(dietRepository, times(1)).save(any(Diet.class));
        verify(dietRepository, times(1)).findByMealDateAndMealTimeAndUser(mealDate, mealTime, user);
    }

    @Test
    @DisplayName("존재하지 않는 유저는 식단에 음식을 추가할 수 없다.")
    public void 존재하지_않는_유저가_식단에_음식_추가시_실패() {
        //given
        DietAddFoodRequestDto requestDto = DietAddFoodRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .apiId(1L)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> dietService.addFood(requestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("식단에 음식을 추가한다.")
    public void 식단에_음식_추가() {
        //given
        DietAddFoodRequestDto requestDto = DietAddFoodRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .apiId(1L)
                .build();

        Diet diet = Mockito.spy(Diet.class);

        when(dietService.findOrCreate(mealDate, mealTime, user)).thenReturn(diet);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(foodService.findOrCreate(anyLong())).thenReturn(food);

        //when
        dietService.addFood(requestDto, 1L);

        //then
        verify(diet, times(1)).addFood(food);
        verify(userRepository, times(1)).findById(1L);

    }

    @Test
    @DisplayName("존재하지 않는 유저는 식단에서 음식을 삭제할 수 없다.")
    void 존재하지_않는_유저가_식단에서_음식_삭제시_실패() {
        //given
        DietRemoveFoodRequestDto requestDto = DietRemoveFoodRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .foodId(1L)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> dietService.removeFood(requestDto, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("식단에서 음식을 삭제한다.")
    void 식단에서_음식_삭제() {
        //given
        DietRemoveFoodRequestDto requestDto = DietRemoveFoodRequestDto.builder()
                .mealDate(mealDate)
                .mealTime(mealTime)
                .foodId(1L)
                .build();

        Diet diet = Mockito.spy(Diet.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(dietRepository.findByMealDateAndMealTimeAndUser(any(LocalDate.class), any(MealTime.class), any(User.class))).thenReturn(Optional.of(diet));
        when(foodService.findById(1L)).thenReturn(food);

        //when
        dietService.removeFood(requestDto, 1L);
      
        //then
        verify(userRepository, times(1)).findById(1L);
        verify(dietRepository, times(1)).findByMealDateAndMealTimeAndUser(mealDate, mealTime, user);
        verify(diet, times(1)).removeFood(food);
    }
}