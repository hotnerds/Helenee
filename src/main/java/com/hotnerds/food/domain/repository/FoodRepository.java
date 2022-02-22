package com.hotnerds.food.domain.repository;

import com.hotnerds.food.domain.Food;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByName(String name);
}
