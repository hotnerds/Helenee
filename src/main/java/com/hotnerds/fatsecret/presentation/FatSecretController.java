package com.hotnerds.fatsecret.presentation;

import static com.hotnerds.fatsecret.presentation.FatSecretController.DEFAULT_URL;

import com.hotnerds.fatsecret.application.FatSecretService;
import com.hotnerds.fatsecret.domain.dto.FatSecretDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DEFAULT_URL)
@RequiredArgsConstructor
public class FatSecretController {

    public static final String DEFAULT_URL = "/search_food";

    private final FatSecretService fatSecretService;

    @GetMapping("/{foodId}")
    public ResponseEntity<FatSecretDetailResponseDto> getFoodById(@PathVariable Long foodId) {
        FatSecretDetailResponseDto response = fatSecretService.getFoodById(foodId);
        return ResponseEntity.ok(response);
    }
}
