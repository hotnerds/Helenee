package com.hotnerds.food.application;

import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.apiclient.FoodApiClient;
import com.hotnerds.food.domain.dto.FoodRequestByNameDto;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import com.hotnerds.food.domain.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    FoodApiClient apiClient;
    FoodRepository foodRepository;

    @Transactional
    public Food findOrCreate(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseGet(() -> apiClient.searchFoodById(foodId));

        return foodRepository.save(food);
    }

    public List<FoodResponseDto> searchFoods(FoodRequestByNameDto requestDto) {
        List<Food> foods = apiClient.searchFoods(requestDto.getFoodName(),
                requestDto.getPageRequest().getPageNumber(),
                requestDto.getPageRequest().getPageSize());

        return foods.stream()
                .map(FoodResponseDto::of)
                .collect(Collectors.toList());
    }
}
