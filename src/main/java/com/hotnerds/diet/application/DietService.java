package com.hotnerds.diet.application;


import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.diet.domain.dto.DietRequestByDateDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import com.hotnerds.diet.domain.dto.DietSaveFoodRequestDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.food.application.FoodService;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietService {

    private final FoodService foodService;

    private final UserRepository userRepository;

    private final DietRepository dietRepository;

    public DietResponseDto find(Long dietId) {
        return DietResponseDto.of(
                dietRepository.findById(dietId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND_EXCEPTION))
        );
    }

    public List<DietResponseDto> searchByDate(DietRequestByDateDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        List<Diet> diets = dietRepository.findAllByMealDateAndUser(requestDto.getMealDate(), user);
        return diets.stream()
                .map(DietResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long saveFoods(DietSaveFoodRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Diet diet = findOrCreate(requestDto.getMealDate(), requestDto.getMealTime(), user);

        diet.clearFood();

        requestDto.getFoods()
                .forEach(e -> diet.addFood(foodService.findOrCreate(e.getFoodId()), e.getAmount()));

        return diet.getId();
    }

    @Transactional
    public Diet findOrCreate(LocalDate mealDate, MealTime mealTime, User user) {
        Diet diet = dietRepository.findByMealDateAndMealTimeAndUser(mealDate, mealTime, user)
                .orElseGet(() -> Diet.builder()
                        .mealDate(mealDate)
                        .mealTime(mealTime)
                        .user(user)
                        .build());

        return dietRepository.save(diet);
    }


}
