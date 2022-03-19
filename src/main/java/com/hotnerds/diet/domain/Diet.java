package com.hotnerds.diet.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.diet.domain.dietFood.DietFoods;
import com.hotnerds.food.domain.Food;
import com.hotnerds.food.domain.Nutrient;
import com.hotnerds.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
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

    @Embedded
    private MealDateTime mealDateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private DietFoods dietFoods = DietFoods.empty();

    @Builder
    public Diet(MealDateTime mealDateTime, User user) {
        this.mealDateTime = mealDateTime;
        this.user = user;
    }
}
