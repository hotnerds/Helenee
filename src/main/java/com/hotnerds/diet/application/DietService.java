package com.hotnerds.diet.application;


import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealDateTime;
import com.hotnerds.diet.domain.dto.DietRequestDto;
import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DietService {

    private final UserService userService;

    private final DietRepository dietRepository;

    public Diet createDiet(DietRequestDto dietRequestDto) {
        User user = userService.getUserById(dietRequestDto.getUserId());
        if (isExistedDiet(dietRequestDto.getMealDateTime(), user)) {
            throw new BusinessException(ErrorCode.DIET_DUPLICATED_EXCEPTION);
        }
        Diet diet = mapToDiet(dietRequestDto);
        dietRequestDto.getFoodList()
                .forEach(e -> diet.addFood(e.getName(), e.getNutrient()));
        return dietRepository.save(diet);
    }

    public Diet updateDiet(Long dietId, DietRequestDto dietRequestDto) {
        User user = userService.getUserById(dietRequestDto.getUserId());
        Diet diet = getDietById(dietId);
        diet.getFoodList().clear();
        dietRequestDto.getFoodList()
                .forEach(e -> diet.addFood(e.getName(), e.getNutrient()));
        return dietRepository.save(diet);
    }

    @Transactional(readOnly = true)
    public boolean isExistedDiet(MealDateTime mealDateTime, User user) {
        return dietRepository.existsByMealDateTimeAndUser(mealDateTime, user);
    }

    @Transactional(readOnly = true)
    public Diet getDietByMealDateTimeAndUser(MealDateTime mealDateTime, User user) {
        return dietRepository.findByMealDateTimeAndUser(mealDateTime, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND_EXCEPTION));
    }

    @Transactional(readOnly = true)
    public Diet getDietById(Long dietId) {
        return dietRepository.findById(dietId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND_EXCEPTION));
    }

    public void deleteDiet(Long dietId) {
        dietRepository.deleteById(dietId);
    }

    public Diet mapToDiet(DietRequestDto dietRequestDto) {
        User user = userService.getUserById(dietRequestDto.getUserId());
        return Diet.builder()
                .mealDateTime(dietRequestDto.getMealDateTime())
                .user(user)
                .nutrient(dietRequestDto.getNutrient())
                .build();
    }
}
