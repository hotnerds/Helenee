package com.hotnerds.diet.domain;

import com.hotnerds.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diet {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;

    @Column
    @Enumerated(EnumType.STRING)
    private MealTimeType mealTime;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder
    public Diet(LocalDate localDate, MealTimeType mealTime, User user) {
        this.localDate = localDate;
        this.mealTime = mealTime;
        this.user = user;
    }
}
