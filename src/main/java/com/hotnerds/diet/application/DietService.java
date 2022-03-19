package com.hotnerds.diet.application;


import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.diet.domain.dto.DietAddFoodRequestDto;
import com.hotnerds.diet.domain.dto.DietReadRequestDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.diet.exception.DietNotFoundException;
import com.hotnerds.food.application.FoodService;
import com.hotnerds.food.domain.Food;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserNotFoundException;
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
                .orElseThrow(UserNotFoundException::new);

        return dietRepository.findByDateTimeUser(requestDto.getMealDate(), requestDto.getMealTime(), user)
                .orElseThrow(DietNotFoundException::new);
    }

    public void addFood(DietAddFoodRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Diet diet = findOrCreate(requestDto.getMealDate(), requestDto.getMealTime(), user);

        Food food = foodService.findOrCreate(requestDto.getApiId());

        diet.addFood(food);
    }

    private Diet findOrCreate(LocalDate mealDate, MealTime mealTime, User user) {
        Diet diet = dietRepository.findByDateTimeUser(mealDate, mealTime, user)
                .orElse(Diet.builder()
                        .mealDate(mealDate)
                        .mealTime(mealTime)
                        .user(user)
                        .build());

        return dietRepository.save(diet);
    }


}
