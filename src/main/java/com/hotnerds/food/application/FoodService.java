package com.hotnerds.food.application;

import com.hotnerds.fatsecret.application.FatSecretApiClient;
import com.hotnerds.food.domain.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodService {

    FatSecretApiClient apiClient;
    FoodRepository foodRepository;
}
