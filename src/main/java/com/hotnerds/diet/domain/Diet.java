package com.hotnerds.diet.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.diet.domain.dietFood.DietFoods;
import com.hotnerds.food.domain.Food;
import com.hotnerds.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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
    @GeneratedValue
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

    public void removeFood(Food food) {
        dietFoods.dissociate(this, food);
    }
}
