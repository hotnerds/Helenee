package com.hotnerds.user.domain.goal;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@AllArgsConstructor
public class Goals {
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<Goal> goalList;

    public void addOrChangeGoal(Goal newGoal) {
        goalList.stream()
                .filter(goal -> goal.equals(newGoal))
                .findFirst()
                .ifPresentOrElse(goal -> goal.change(newGoal), () -> goalList.add(newGoal));
    }

    public Goal getGoalForDate(LocalDate date) {
        return goalList.stream()
                .filter(g -> g.getDate().equals(date))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND_EXCEPTION));
    }

    public static Goals empty() {
        return new Goals(new ArrayList<>());
    }
}
