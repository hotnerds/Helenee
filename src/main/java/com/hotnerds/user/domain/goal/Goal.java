package com.hotnerds.user.domain.goal;

import com.hotnerds.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double calories;

    private Double carbs;

    private Double protein;

    private Double fat;

    private LocalDate date;

    public void change(Goal newGoal) {
        this.calories = newGoal.calories;
        this.carbs = newGoal.carbs;
        this.protein = newGoal.protein;
        this.fat = newGoal.fat;
    }

    @Builder
    public Goal(Double calories, Double carbs, Double protein, Double fat, LocalDate date) {
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return date.equals(goal.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
