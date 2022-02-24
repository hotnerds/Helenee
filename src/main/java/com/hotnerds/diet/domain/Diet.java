package com.hotnerds.diet.domain;

import com.hotnerds.common.BaseTimeEntity;
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
public class Diet extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "DIET_ID")
    private Long dietId;

    @Embedded
    private MealDateTime mealDateTime;

    @Embedded
    private Nutrient nutrient;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "diet", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Food> foodList = new ArrayList<>();

    @Builder
    public Diet(MealDateTime mealDateTime, Nutrient nutrient, User user) {
        this.mealDateTime = mealDateTime;
        this.nutrient = nutrient;
        this.user = user;
    }

    public void addFood(String name, Nutrient nutrient) {
        this.foodList.add(Food.builder()
                .name(name)
                .nutrient(nutrient)
                .diet(this)
                .build());
    }
}
