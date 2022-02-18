package com.hotnerds.diet.domain.repoistory;

import com.hotnerds.diet.domain.DietFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietFoodRepository extends JpaRepository<DietFood, Long> {
}
