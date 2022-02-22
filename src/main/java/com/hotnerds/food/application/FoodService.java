package com.hotnerds.food.application;

import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.dto.FoodRequestDto;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import com.hotnerds.food.domain.repository.FoodRepository;
import com.hotnerds.food.exception.FoodAlreadyExistsException;
import com.hotnerds.food.exception.FoodNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodResponseDto getFoodById(Long foodId) {
        Food food = foodRepository.findById(foodId)
            .orElseThrow(FoodNotFoundException::new);

        return FoodResponseDto.of(food);
    }

    public void saveFood(FoodRequestDto foodRequestDto) {
        Optional<Food> optionalFood = foodRepository.findByName(foodRequestDto.getName());
        if (optionalFood.isPresent()) {
            throw new FoodAlreadyExistsException();
        }

        foodRepository.save(foodRequestDto.toEntity());
    }
}
