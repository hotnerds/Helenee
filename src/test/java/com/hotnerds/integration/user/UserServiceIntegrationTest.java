package com.hotnerds.integration.user;

import com.hotnerds.integration.IntegrationTest;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.dto.*;
import com.hotnerds.user.domain.goal.Goal;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceIntegrationTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    List<NewUserReqDto> actualNewUserReqDtoList = Arrays.asList(
            NewUserReqDto.builder()
                    .username("RetepMil")
                    .email("lkslyj2@naver.com")
                    .build(),
            NewUserReqDto.builder()
                    .username("PeterLim")
                    .email("lkslyj8@naver.com")
                    .build()
    );

    User user1;
    User user2;
    Follow follow;
    FollowServiceReqDto reqDto;
    Goal goal;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .build();

        user2 = User.builder()
                .username("user2")
                .email("user2@gmail.com")
                .build();

        reqDto = FollowServiceReqDto.builder()
                .followerId(1L) // given id for user1
                .followedId(2L) // given id for user2
                .build();

        follow = Follow.builder()
                .follower(user1)
                .followed(user2)
                .build();

        goal = Goal.builder()
                .calories(1.0)
                .carbs(1.0)
                .protein(1.0)
                .fat(1.0)
                .date(LocalDate.of(2022, 3, 30))
                .user(user1)
                .build();
    }

    @DisplayName("생성하려는 유저가 이미 존재하면 에러 발생")
    @Test
    void 유저_생성_실패() {
        //given
        NewUserReqDto newUserReqDto = actualNewUserReqDtoList.get(0);
        User user = newUserReqDto.toEntity();
        userRepository.save(user);

        //when then
        assertThatThrownBy(() -> userService.createNewUser(newUserReqDto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_DUPLICATED_EXCEPTION);
    }

    @DisplayName("신규 유저 생성 - 성공")
    @Test
    void createNewUser() {
        //given
        NewUserReqDto newUserReqDto = actualNewUserReqDtoList.get(0);

        //when
        userService.createNewUser(newUserReqDto);

        //then
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("유저 전체 조회 - 성공")
    void getAllUsers() {
        // given
        userRepository.save(user1);
        userRepository.save(user2);

        // when
        List<User> gotUserList = userService.getAllUsers();

        // then
        assertThat(gotUserList).hasSize(2);
    }

    @DisplayName("ID로 유저 조회 - 성공")
    @Test
    void getUserById() {
        // given
        User user = userRepository.save(user1);
        Long userId = user.getId();

        // when
        User userFound = userService.getUserById(userId);

        // then
        assertThat(user).isEqualTo(userFound);
    }

    @DisplayName("존재하지 않는 유저 삭제 - 실패")
    @Test
    void 유저_삭제_실패() {
        //given

        //when then
        assertThatThrownBy(() -> userService.deleteUserById(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("유저 삭제 - 성공")
    @Test
    void 유저_삭제_성공() {
        //given
        User user = userRepository.save(user1);
        Long userId = user.getId();

        //when
        userService.deleteUserById(1L);

        //then
        assertThat(userRepository.findAll()).isEmpty();
    }

    @DisplayName("유저 정보 변경 - 성공")
    @Test
    void updateUser() {
        // given
        User user = userRepository.save(user1);
        UserUpdateReqDto userUpdateReqDto = new UserUpdateReqDto("GARAM");

        // when
        userService.updateUser(user.getId(), userUpdateReqDto);

        // then
        assertThat(user.getUsername()).isEqualTo("GARAM");
    }

    @DisplayName("팔로우 유청 - 성공")
    @Test
    void 팔로우_요청_성공() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        FollowServiceReqDto requestDto = FollowServiceReqDto.builder()
                .followerId(user1.getId())
                .followedId(user2.getId())
                .build();

        //when
        userService.createFollow(requestDto);

        //then
        assertThat(userService.isFollowExist(user1, user2)).isTrue();
    }

    @DisplayName("중복 팔로우 요청 - 실패")
    @Test
    void 중복_팔로우_요청_실패() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        FollowServiceReqDto requestDto = FollowServiceReqDto.builder()
                .followerId(user1.getId())
                .followedId(user2.getId())
                .build();

        //when
        userService.createFollow(requestDto);

        //then
        assertThatThrownBy(() -> userService.createFollow(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.FOLLOW_DUPLICATED_EXCEPTION.getMessage());
    }

    @DisplayName("팔로워 리스트 조회 - 실패")
    @Test
    void 팔로워_리스트_조회_실패() {
        //given

        //when then
        assertThatThrownBy(() -> userService.getUserFollowers(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("팔로워 리스트 조회 - 성공")
    void 팔로워_리스트_조회() {
        //given
        User user3 = User.builder()
                .username("user3")
                .email("user3@gmail.com")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        FollowServiceReqDto requestDto1 = FollowServiceReqDto.builder()
                .followerId(user2.getId())
                .followedId(user1.getId())
                .build();
        FollowServiceReqDto requestDto2 = FollowServiceReqDto.builder()
                .followerId(user3.getId())
                .followedId(user1.getId())
                .build();

        userService.createFollow(requestDto1);
        userService.createFollow(requestDto2);

        //when
        List<FollowUserInfoResponseDto> followers = userService.getUserFollowers(user1.getId());

        //then
        assertThat(followers).hasSize(2);
    }

    @DisplayName("팔로잉 리스트 조회 - 실패")
    @Test
    void 팔로잉_리스트_조회_실패() {
        //given

        //when then
        assertThatThrownBy(() -> userService.getUserFollowings(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("팔로잉 리스트 조회 성공")
    @Test
    void 팔로잉_리스트_조회_성공() {
        //given
        User user3 = User.builder()
                .username("user3")
                .email("user3@gmail.com")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        FollowServiceReqDto requestDto1 = FollowServiceReqDto.builder()
                .followerId(user1.getId())
                .followedId(user2.getId())
                .build();
        FollowServiceReqDto requestDto2 = FollowServiceReqDto.builder()
                .followerId(user1.getId())
                .followedId(user3.getId())
                .build();

        userService.createFollow(requestDto1);
        userService.createFollow(requestDto2);

        //when
        List<FollowUserInfoResponseDto> followings = userService.getUserFollowings(user1.getId());

        //then
        assertThat(followings).hasSize(2);
    }

    @DisplayName("팔로우 취소 - 성공")
    @Test
    void 팔로우_취소_성공() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        FollowServiceReqDto requestDto = FollowServiceReqDto.builder()
                .followerId(user1.getId())
                .followedId(user2.getId())
                .build();
        userService.createFollow(requestDto);

        //when
        userService.deleteFollow(requestDto);

        //then
        assertThat(userService.isFollowExist(user1, user2)).isFalse();
    }

    @Test
    @DisplayName("팔로워 수 확인 - 성공")
    void 유저_팔로워_수_확인() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        FollowServiceReqDto requestDto = FollowServiceReqDto.builder()
                .followerId(user1.getId())
                .followedId(user2.getId())
                .build();
        userService.createFollow(requestDto);

        // when
        Integer count = userService.getFollowerCounts(user2.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("팔로잉 수 확인 - 성공")
    void 유저_팔로잉_수() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        FollowServiceReqDto requestDto = FollowServiceReqDto.builder()
                .followerId(user1.getId())
                .followedId(user2.getId())
                .build();
        userService.createFollow(requestDto);

        // when
        Integer count = userService.getFollowCounts(user1.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("유저는 목표를 생성할 수 있다.")
    @Test
    void 목표_생성_성공() {
        //given
        userRepository.save(user1);
        GoalRequestDto requestDto = GoalRequestDto.builder()
                .calories(goal.getCalories())
                .carbs(goal.getCarbs())
                .protein(goal.getProtein())
                .fat(goal.getFat())
                .date(goal.getDate())
                .build();

        //when
        userService.createOrChangeGoal(requestDto, user1.getUsername());

        //then
        assertThat(user1.getGoals().getGoalList()).hasSize(1);

    }

    @DisplayName("존재하지 않은 유저는 목표를 생성할 수 없다.")
    @Test
    void 존재하지_않은_유저_목표_생성_실패() {
        //given
        GoalRequestDto requestDto = GoalRequestDto.builder()
                .calories(goal.getCalories())
                .carbs(goal.getCarbs())
                .protein(goal.getProtein())
                .fat(goal.getFat())
                .date(goal.getDate())
                .build();

        String username = user1.getUsername();

        //when then
        assertThatThrownBy(() -> userService.createOrChangeGoal(requestDto, username))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("유저는 특정 날짜에 세운 목표를 조회할 수 있다.")
    @Test
    void 특정_날짜_목표_조회_성공() {
        //given
        userRepository.save(user1);
        user1.addOrChangeGoal(goal);

        //when
        GoalResponseDto response = userService.findGoalByDate(goal.getDate(), user1.getUsername());

        //then
        assertThat(response.getDate()).isEqualTo(goal.getDate());
    }

    @DisplayName("존재하지 않은 유저 목표 조회시 예외 발생")
    @Test
    void 존재하지_않은_유저_목표_조회_실패() {
        //given
        user1.addOrChangeGoal(goal);
        String username = user1.getUsername();
        LocalDate date = goal.getDate();
        //when then
        assertThatThrownBy(() -> userService.findGoalByDate(date, username))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("특정 날짜 목표 조회 시 목표가 없으면 예외 발생")
    @Test
    void 존재하지_않은_목표_조회_실패() {
        //given
        userRepository.save(user1);
        String username = user1.getUsername();
        LocalDate date = goal.getDate();
        //when then
        assertThatThrownBy(() -> userService.findGoalByDate(date, username))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.GOAL_NOT_FOUND_EXCEPTION);
    }
}