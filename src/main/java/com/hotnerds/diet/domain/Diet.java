package com.hotnerds.diet.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.diet.domain.dietFood.DietFoods;
import com.hotnerds.food.domain.Food;
import com.hotnerds.post.domain.like.Like;
import com.hotnerds.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"meal_date", "meal_time", "user_id"}
        )
})
public class Diet extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long dietId;

    @Column(name = "meal_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mealDate;

    @Column(name = "meal_time", nullable = false)
    @Enumerated(EnumType.STRING)
    private MealTime mealTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private DietFoods dietFoods = DietFoods.empty();

    @Builder
    public Diet(LocalDate mealDate, MealTime mealTime, User user) {
        this.mealDate = mealDate;
        this.mealTime = mealTime;
        this.user = user;
    }

    public List<Food> getFoods() {
        return dietFoods.getFoods();
    }

    public void addFood(Food food) {
        dietFoods.associate(this, food);
    }

    public void clearFood() {
        dietFoods.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Diet diet = (Diet) obj;
        return Objects.equals(mealDate, diet.mealDate)
                && Objects.equals(mealTime, diet.mealTime)
                && Objects.equals(user, diet.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealDate, mealTime, user);
    }
}
