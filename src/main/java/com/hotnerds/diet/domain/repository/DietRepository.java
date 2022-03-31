package com.hotnerds.diet.domain.repository;

import com.hotnerds.diet.domain.Diet;
import com.hotnerds.diet.domain.MealTime;
import com.hotnerds.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DietRepository extends JpaRepository<Diet, Long> {

    Optional<Diet> findByMealDateAndMealTimeAndUser(LocalDate mealDate, MealTime mealTime, User user);

    List<Diet> findAllByMealDateAndUser(LocalDate mealDate, User user);
}
