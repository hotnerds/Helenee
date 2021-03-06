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

    @DisplayName("??????????????? ????????? ?????? ???????????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
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

    @DisplayName("?????? ?????? ?????? - ??????")
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
    @DisplayName("?????? ?????? ?????? - ??????")
    void getAllUsers() {
        // given
        userRepository.save(user1);
        userRepository.save(user2);

        // when
        List<User> gotUserList = userService.getAllUsers();

        // then
        assertThat(gotUserList).hasSize(2);
    }

    @DisplayName("ID??? ?????? ?????? - ??????")
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

    @DisplayName("???????????? ?????? ?????? ?????? - ??????")
    @Test
    void ??????_??????_??????() {
        //given

        //when then
        assertThatThrownBy(() -> userService.deleteUserById(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("?????? ?????? - ??????")
    @Test
    void ??????_??????_??????() {
        //given
        User user = userRepository.save(user1);
        Long userId = user.getId();

        //when
        userService.deleteUserById(1L);

        //then
        assertThat(userRepository.findAll()).isEmpty();
    }

    @DisplayName("?????? ?????? ?????? - ??????")
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

    @DisplayName("????????? ?????? - ??????")
    @Test
    void ?????????_??????_??????() {
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

    @DisplayName("?????? ????????? ?????? - ??????")
    @Test
    void ??????_?????????_??????_??????() {
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

    @DisplayName("????????? ????????? ?????? - ??????")
    @Test
    void ?????????_?????????_??????_??????() {
        //given

        //when then
        assertThatThrownBy(() -> userService.getUserFollowers(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("????????? ????????? ?????? - ??????")
    void ?????????_?????????_??????() {
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

    @DisplayName("????????? ????????? ?????? - ??????")
    @Test
    void ?????????_?????????_??????_??????() {
        //given

        //when then
        assertThatThrownBy(() -> userService.getUserFollowings(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("????????? ????????? ?????? ??????")
    @Test
    void ?????????_?????????_??????_??????() {
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

    @DisplayName("????????? ?????? - ??????")
    @Test
    void ?????????_??????_??????() {
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
    @DisplayName("????????? ??? ?????? - ??????")
    void ??????_?????????_???_??????() {
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
    @DisplayName("????????? ??? ?????? - ??????")
    void ??????_?????????_???() {
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

    @DisplayName("????????? ????????? ????????? ??? ??????.")
    @Test
    void ??????_??????_??????() {
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

    @DisplayName("???????????? ?????? ????????? ????????? ????????? ??? ??????.")
    @Test
    void ????????????_??????_??????_??????_??????_??????() {
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

    @DisplayName("????????? ?????? ????????? ?????? ????????? ????????? ??? ??????.")
    @Test
    void ??????_??????_??????_??????_??????() {
        //given
        userRepository.save(user1);
        user1.addOrChangeGoal(goal);

        //when
        GoalResponseDto response = userService.findGoalByDate(goal.getDate(), user1.getUsername());

        //then
        assertThat(response.getDate()).isEqualTo(goal.getDate());
    }

    @DisplayName("???????????? ?????? ?????? ?????? ????????? ?????? ??????")
    @Test
    void ????????????_??????_??????_??????_??????_??????() {
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

    @DisplayName("?????? ?????? ?????? ?????? ??? ????????? ????????? ?????? ??????")
    @Test
    void ????????????_??????_??????_??????_??????() {
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