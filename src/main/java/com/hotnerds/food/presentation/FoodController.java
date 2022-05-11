package com.hotnerds.food.presentation;

import com.hotnerds.food.application.FoodService;
import com.hotnerds.food.domain.dto.FoodRequestByNameDto;
import com.hotnerds.food.domain.dto.FoodResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.hotnerds.food.presentation.FoodController.DEFAULT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class FoodController {

    public static final String DEFAULT_URL = "/api/foods";

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodResponseDto>> searchFoods(FoodRequestByNameDto requestDto, @PageableDefault Pageable pageable) {
        requestDto.setPageAble(pageable);
        return ResponseEntity.ok(foodService.searchFoods(requestDto));
    }
}
