package com.hotnerds.diet.presentation;

import com.hotnerds.diet.application.DietService;
import com.hotnerds.diet.domain.dto.DietRequestDto;
import com.hotnerds.diet.domain.dto.DietResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hotnerds.diet.presentation.DietController.DEFAULT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class DietController {

    public static final String DEFAULT_URL = "/diets";

    private final DietService dietService;

    @GetMapping("/{dietId}")
    public ResponseEntity<DietResponseDto> getDietById(@PathVariable Long dietId) {
        return ResponseEntity.ok(DietResponseDto.of(dietService.getDietById(dietId)));
    }

    @PostMapping("/")
    public ResponseEntity<DietResponseDto> createDiet(@RequestBody DietRequestDto dietRequestDto) {
        return ResponseEntity.ok(DietResponseDto.of(dietService.createDiet(dietRequestDto)));
    }

    @PatchMapping("/{dietId}")
    public ResponseEntity<DietResponseDto> updateDiet(@PathVariable Long dietId, @RequestBody DietRequestDto dietRequestDto) {
        return ResponseEntity.ok(DietResponseDto.of(dietService.updateDiet(dietId, dietRequestDto)));
    }

    @DeleteMapping("/{dietId}")
    public ResponseEntity<Void> deleteDiet(@PathVariable Long dietId) {
        dietService.deleteDiet(dietId);

        return ResponseEntity.noContent().build();
    }


}
