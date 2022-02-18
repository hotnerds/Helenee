package com.hotnerds.diet.domain;

import com.hotnerds.common.BaseTimeEntity;
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
public class Diet extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "DIET_ID")
    private Long dietId;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;

    @Column
    @Enumerated(EnumType.STRING)
    private MealTimeType mealTime;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column
    private Double totalCalories;

    @Column
    private Double totalCarbs;

    @Column
    private Double totalProtein;

    @Column
    private Double totalFat;

    @OneToMany
    @JoinColumn(name = "DIETFOOD_ID")
    private List<DietFood> dietFoodList;

    @Builder
    public Diet(LocalDate localDate, MealTimeType mealTime, User user) {
        this.localDate = localDate;
        this.mealTime = mealTime;
        this.user = user;
    }
}
