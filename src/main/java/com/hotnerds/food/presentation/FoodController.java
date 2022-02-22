package com.hotnerds.food.presentation;

import com.hotnerds.food.application.FoodService;
import com.hotnerds.food.domain.dto.FoodRequestDto;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hotnerds.food.presentation.FoodController.DEFAULT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class FoodController {

    public static final String DEFAULT_URL = "/foods";
    private final FoodService foodService;

    @GetMapping("/{foodId}")
    public ResponseEntity<FoodResponseDto> getFood(@PathVariable Long foodId) {
        FoodResponseDto foodResponseDto = foodService.getFoodById(foodId);

        return ResponseEntity.ok(foodResponseDto);
    }
}
