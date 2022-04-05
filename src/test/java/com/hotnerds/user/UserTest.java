package com.hotnerds.user;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.common.security.oauth2.service.AuthProvider;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.goal.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


class UserTest {

    User user;

    Goal goal;

    @BeforeEach
    void init() {
        user = User.builder()
                .username("garam")
                .email("kgr4163@naver.com")
                .build();

        goal = Goal.builder()
                .calories(1.0)
                .carbs(1.0)
                .protein(1.0)
                .fat(1.0)
                .date(LocalDate.of(2022, 3, 30))
                .build();
    }

    @DisplayName("사용자는 목표를 갖을 수 있다.")
    @Test
    void 목표_생성_성공() {
        user.addOrChangeGoal(goal);

        assertThat(user.getGoals().getGoalList()).hasSize(1);
    }

    @DisplayName("같은 날짜에 이미 등록된 목표가 있다면 목표를 수정 한다.")
    @Test
    void 목표_수정_성공() {
        user.addOrChangeGoal(goal);

        Goal newGoal = Goal.builder()
                .calories(2.0)
                .carbs(2.0)
                .protein(2.0)
                .fat(2.0)
                .date(goal.getDate())
                .build();

        user.addOrChangeGoal(newGoal);

        Goal findGoal = user.getGoalOfUser(newGoal.getDate());

        assertThat(findGoal)
                .usingRecursiveComparison()
                .isEqualTo(newGoal);
    }

    @DisplayName("특정 날짜에 찾으려는 목표가 없을 때 예외를 발생시킨다.")
    @Test
    void 찾으려는_목표_없을때_예외_발생() {
        LocalDate target = LocalDate.of(2022, 3, 22);
        assertThatThrownBy(() -> user.getGoalOfUser(target))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.GOAL_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("사용자는 가입한 플랫폼의 정보를 가질 수 있다.")
    @Test
    void 플랫폼_정보_조회() {
        User newUser = new User("garam", "kgr4163@gmail.com", ROLE.USER, AuthProvider.KAKAO);

        assertThat(newUser.getRegistrationId()).isEqualTo(AuthProvider.KAKAO.getRegistrationId());
    }
}
