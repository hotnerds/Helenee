package com.hotnerds.user.domain.goal;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@AllArgsConstructor
public class Goals {
    private List<Goal> goals;

    public void addOrChangeGoal(Goal newGoal) {
        goals.stream()
                .filter(goal -> goal.equals(newGoal))
                .findFirst()
                .ifPresentOrElse(goal -> goal.change(newGoal), () -> goals.add(newGoal));
    }

    public Goal getGoalForDate(LocalDate date) {
        return goals.stream()
                .filter(g -> g.getDate().equals(date))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND_EXCEPTION));
    }

    public static Goals empty() {
        return new Goals(new ArrayList<>());
    }
}
