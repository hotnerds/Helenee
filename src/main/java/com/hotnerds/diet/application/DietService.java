package com.hotnerds.diet.application;


import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.diet.domain.dto.DietAddFoodRequestDto;
import com.hotnerds.diet.domain.dto.DietReadRequestDto;
import com.hotnerds.diet.domain.dto.DietRemoveFoodRequestDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.food.application.FoodService;
import com.hotnerds.food.domain.Food;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietService {

    private final FoodService foodService;

    private final UserRepository userRepository;

    private final DietRepository dietRepository;

    public Diet findByDateTimeUser(DietReadRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        return dietRepository.findByDateTimeUser(requestDto.getMealDate(), requestDto.getMealTime(), user)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND_EXCEPTION));
    }

    @Transactional
    public void addFood(DietAddFoodRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Diet diet = findOrCreate(requestDto.getMealDate(), requestDto.getMealTime(), user);

        Food food = foodService.findOrCreate(requestDto.getApiId());

        diet.addFood(food);
    }

    @Transactional
    public void removeFood(DietRemoveFoodRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Diet diet = dietRepository.findByDateTimeUser(requestDto.getMealDate(), requestDto.getMealTime(), user)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND_EXCEPTION));

        Food food = foodService.findById(requestDto.getFoodId());

        diet.removeFood(food);
    }

    @Transactional
    protected Diet findOrCreate(LocalDate mealDate, MealTime mealTime, User user) {
        Diet diet = dietRepository.findByDateTimeUser(mealDate, mealTime, user)
                .orElseGet(() -> Diet.builder()
                        .mealDate(mealDate)
                        .mealTime(mealTime)
                        .user(user)
                        .build());

        return dietRepository.save(diet);
    }


}
