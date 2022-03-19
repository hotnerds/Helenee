package com.hotnerds.diet.domain.repository;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealDateTime;
import com.hotnerds.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Long> {

    Optional<Diet> findByMealDateTimeAndUser(MealDateTime mealDateTime, User user);

    boolean existsByMealDateTimeAndUser(MealDateTime mealDateTime, User user);
}
