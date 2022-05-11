package com.hotnerds.unit.user.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.dto.*;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.FollowedList;
import com.hotnerds.user.domain.FollowerList;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.goal.Goal;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
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
        NewUserReqDto newUserReqDto = actualNewUserReqDtoList.get(0);
        User user = newUserReqDto.toEntity();
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.createNewUser(newUserReqDto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_DUPLICATED_EXCEPTION);
        verify(userRepository, times(1)).findByUsernameOrEmail(anyString(), anyString());
    }

    @Test
    void createNewUser() {
        // mocking
        NewUserReqDto newUserReqDto = actualNewUserReqDtoList.get(0);
        User user = newUserReqDto.toEntity();
        when(userRepository.save(any())).thenReturn(user);

        // when
        userService.createNewUser(newUserReqDto);
        verify(userRepository, times(1)).findByUsernameOrEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("유저 전체 조회")
    void getAllUsers() {
        // mocking
        when(userRepository.findAll()).thenReturn(actualNewUserReqDtoList.stream()
                .map(NewUserReqDto::toEntity)
                .collect(Collectors.toList()));

        // when
        List<User> gotUserList = userService.getAllUsers();

        // then
        assertEquals(2, gotUserList.size());
        assertTrue(actualNewUserReqDtoList.get(0).toEntity()
                .equals(gotUserList.get(0)));
        assertTrue(actualNewUserReqDtoList.get(1).toEntity()
                .equals(gotUserList.get(1)));
    }

    @Test
    void getUserById() {
        // mocking
        User user = actualNewUserReqDtoList.get(0).toEntity();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        User userFound = userService.getUserById(user.getId());

        // then
        assertEquals(user, userFound);
    }

    @DisplayName("존재하지 않는 유저를 삭제하면 예외 발생")
    @Test
    void 유저_삭제_실패() {
        NewUserReqDto newUserReqDto = actualNewUserReqDtoList.get(0);
        assertThatThrownBy(() -> userService.deleteUserById(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @DisplayName("유저 삭제 성공")
    @Test
    void 유저_삭제_성공() {
        NewUserReqDto newUserReqDto = actualNewUserReqDtoList.get(0);
        User user = newUserReqDto.toEntity();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void updateUser() {
        // mocking
        UserUpdateReqDto userUpdateReqDto = new UserUpdateReqDto("GARAM");
        User user = actualNewUserReqDtoList.get(0).toEntity();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        userService.updateUser(user.getId(), userUpdateReqDto);

        // then
    }

    @DisplayName("isFollowExist 함수에 대한 기능 테스트")
    @Test
    void isFollowExist() {
        user1 = spy(user1);
        user2 = spy(user2);
        User user3 = new User("user3", "email");
        User user4 = new User("user4", "email");
        User user5 = new User("user5", "email");
        Follow follow = new Follow(user1, user2);
        Follow follow2 = new Follow(user1, user3);
        Follow follow3 = new Follow(user1, user4);

        // user 1 and 2 correctly have the follow relationship
        user1.getFollowedList().getFollowed().add(follow);
        user2.getFollowerList().getFollowers().add(follow);

        // user 1 has the follow relationship follow2, but user3 doesn't
        user1.getFollowedList().getFollowed().add(follow2);

        // user 4 has the follow relationship follow3, but user1 doesn't
        user4.getFollowerList().getFollowers().add(follow3);

        //there are no follow relationship between user1 and user5

        assertThat(userService.isFollowExist(user1, user2)).isTrue();
        assertThat(userService.isFollowExist(user1, user3)).isFalse();
        assertThat(userService.isFollowExist(user1, user4)).isFalse();
        assertThat(userService.isFollowExist(user1, user5)).isFalse();
    }

    @DisplayName("followCheck 함수에 대한 기능 테스트")
    @Test
    void followCheck() {
        Follow follow = new Follow(user1, user2);

        user1.getFollowedList().getFollowed().add(follow);
        user2.getFollowerList().getFollowers().add(follow);

        FollowServiceReqDto reqDto = new FollowServiceReqDto(1L, 2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        assertThat(userService.followCheck(reqDto)).isTrue();
    }

    @Test
    @DisplayName("새로운 팔로우 관계를 생성할때 같은 관계가 존재하면 예외 발생")
    void 팔로우_중복_확인() {
        // given
        user1.getFollowedList().add(follow);
        user2.getFollowerList().add(follow);

        when(userRepository.findById(reqDto.getFollowerId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(reqDto.getFollowedId())).thenReturn(Optional.of(user2));

        // when then
        assertThatThrownBy(() -> userService.createFollow(reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.FOLLOW_DUPLICATED_EXCEPTION.getMessage());
        verify(userRepository, times(1)).findById(reqDto.getFollowerId());
        verify(userRepository, times(1)).findById(reqDto.getFollowedId());
    }

    @Test
    @DisplayName("새로운 팔로우 관계를 생성할 수 있다")
    void 새로운_팔로우_관계_생성() {
        // given
        when(userRepository.findById(reqDto.getFollowerId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(reqDto.getFollowedId())).thenReturn(Optional.of(user2));

        // when
        Follow response = userService.createFollow(reqDto);

        // then
        assertTrue(follow.getFollower() == response.getFollower() &&
                follow.getFollowed() == response.getFollowed());
        verify(userRepository, times(1)).findById(reqDto.getFollowerId());
        verify(userRepository, times(1)).findById(reqDto.getFollowedId());
    }

    @Test
    @DisplayName("존재하지 않는 id를 가진 유저를 팔로잉하는 모든 유저들의 id를 검색하면 예외발생")
    public void 유저_팔로워_리스트_오류() {
        // when then
        assertThatThrownBy(() -> userService.getUserFollowers(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("특정 id의 유저를 팔로잉하는 모든 유저들의 id를 검색 가능")
    public void 유저_팔로워_리스트_검색() {
        // given
        User mockedUser1 = mock(User.class);
        User mockedUser2 = mock(User.class);
        User mockedUser3 = mock(User.class);
        FollowerList mockedList = mock(FollowerList.class);

        follow = Follow.builder()
                .follower(mockedUser1)
                .followed(mockedUser2)
                .build();

        Follow anotherFollow = Follow.builder()
                .follower(mockedUser3)
                .followed(mockedUser2)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(mockedUser2));
        when(mockedUser1.getId()).thenReturn(1L);
        when(mockedUser3.getId()).thenReturn(3L);
        when(mockedUser2.getFollowerList()).thenReturn(mockedList);
        when(mockedList.getFollowers()).thenReturn(Arrays.asList(follow, anotherFollow));

        List<Long> expectedList = List.of(1L, 3L);

        // when
        List<FollowUserInfoResponseDto> userList = userService.getUserFollowers(2L); // 2 is user id for user2

        // then
        assertEquals(expectedList.get(0), userList.get(0).getUserId());
        assertEquals(expectedList.get(1), userList.get(1).getUserId());
        verify(userRepository).findById(2L);
    }

    @Test
    @DisplayName("존재하지 않는 id를 가진 유저가 팔로잉하는 모든 유저들의 id를 검색하면 예외발생")
    public void 유저_팔로잉_리스트_오류() {
        // when then
        assertThatThrownBy(() -> userService.getUserFollowings(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("특정 id의 유저가 팔로잉하는 유저들의 모든 id를 검색 가능")
    public void 유저_팔로잉_리스트_검색() {
        // given
        User mockedUser1 = mock(User.class);
        User mockedUser2 = mock(User.class);
        User mockedUser3 = mock(User.class);
        FollowedList mockedList = mock(FollowedList.class);

        follow = Follow.builder()
                .follower(mockedUser2)
                .followed(mockedUser1)
                .build();

        Follow anotherFollow = Follow.builder()
                .follower(mockedUser2)
                .followed(mockedUser3)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(mockedUser2));
        when(mockedUser1.getId()).thenReturn(1L);
        when(mockedUser3.getId()).thenReturn(3L);
        when(mockedUser2.getFollowedList()).thenReturn(mockedList);
        when(mockedList.getFollowed()).thenReturn(Arrays.asList(follow, anotherFollow));

        List<Long> expectedList = List.of(1L, 3L);

        // when
        List<FollowUserInfoResponseDto> userList = userService.getUserFollowings(2L); // 2 is user id for user2

        // then
        assertEquals(expectedList.get(0), userList.get(0).getUserId());
        assertEquals(expectedList.get(1), userList.get(1).getUserId());
        verify(userRepository).findById(2L);
    }

    @Test
    @DisplayName("특정 id를 가진 유저의 현재 팔로워 수 응답 가능")
    public void 유저_팔로워_수() {
        // given
        User user3 = User.builder()
                .username("user3")
                .email("user3@gmail.com")
                .build();

        Follow anotherFollow = Follow.builder()
                .follower(user3)
                .followed(user2)
                .build();

        user1.getFollowedList().getFollowed().add(follow);
        user2.getFollowerList().getFollowers().add(follow);
        user3.getFollowedList().getFollowed().add(anotherFollow);
        user2.getFollowerList().getFollowers().add(anotherFollow);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        // when
        Integer count = userService.getFollowerCounts(2L);

        // then
        assertEquals(2, count);
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("특정 id를 가진 유저의 현재 팔로워 수 응답 가능")
    public void 유저_팔로잉_수() {
        User user3 = User.builder()
                .username("user3")
                .email("user3@gmail.com")
                .build();

        Follow anotherFollow = Follow.builder()
                .follower(user3)
                .followed(user2)
                .build();

        user1.getFollowedList().getFollowed().add(follow);
        user2.getFollowerList().getFollowers().add(follow);
        user3.getFollowedList().getFollowed().add(anotherFollow);
        user2.getFollowerList().getFollowers().add(anotherFollow);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        // when
        Integer count = userService.getFollowCounts(2L);

        // then
        assertEquals(0, count);
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("어떤 유저가 다른 유저를 팔로우하고 있는지 확인 가능")
    public void 유저_팔로우_확인() {
        // given
        user1.getFollowedList().getFollowed().add(follow);
        user2.getFollowerList().getFollowers().add(follow);

        // when
        boolean expectTrue = userService.isFollowExist(user1, user2);

        // then
        assertTrue(expectTrue);
    }

    @Test
    @DisplayName("서로 팔로우 되어있는지 확인하는 기능")
    public void 유저_뮤추얼_팔로우_확인() {
        User user3 = User.builder()
                .username("user3")
                .email("user3@gmail.com")
                .build();

        Follow mutualFollow = Follow.builder()
                .follower(user2)
                .followed(user1)
                .build();

        Follow anotherFollow = Follow.builder()
                .follower(user3)
                .followed(user2)
                .build();

        user1.getFollowedList().getFollowed().add(follow);
        user2.getFollowerList().getFollowers().add(follow);
        user2.getFollowedList().getFollowed().add(mutualFollow);
        user1.getFollowerList().getFollowers().add(mutualFollow);
        user3.getFollowedList().getFollowed().add(anotherFollow);
        user2.getFollowerList().getFollowers().add(anotherFollow);

        // when
        boolean expectMutualTrue = userService.isFollowExist(user1, user2) && userService.isFollowExist(user2, user1);
        boolean expectMutualFalse = userService.isFollowExist(user1, user3) && userService.isFollowExist(user3, user1);

        // then
        assertTrue(expectMutualTrue);
        assertFalse(expectMutualFalse);
    }
    
    @Test
    @DisplayName("팔로우가 되어 있지 않은 유저에 대한 팔로우 관계 취소 요청이 오면 오류 발생")
    public void 유저_팔로우_취소_오류() {
        // given
        when(userRepository.findById(reqDto.getFollowerId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(reqDto.getFollowedId())).thenReturn(Optional.of(user2));

        // when then
        assertThatThrownBy(() -> userService.deleteFollow(reqDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.FOLLOW_NOT_FOUND_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("팔로우 관계 취소 기능이 되어야 한다")
    public void 유저_팔로우_취소() {
        // given
        user1.getFollowedList().add(follow);
        user2.getFollowerList().add(follow);
        when(userRepository.findById(reqDto.getFollowerId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(reqDto.getFollowedId())).thenReturn(Optional.of(user2));

        // when
        userService.deleteFollow(reqDto);

        // then
        assertAll(
                () -> assertEquals(0, user1.getFollowedList().followCounts()),
                () -> assertEquals(0, user2.getFollowerList().followerCounts())
        );
    }

    @DisplayName("유저는 목표를 생성할 수 있다.")
    @Test
    void 목표_생성_성공() {
        //given
        GoalRequestDto requestDto = GoalRequestDto.builder()
                .calories(goal.getCalories())
                .carbs(goal.getCarbs())
                .protein(goal.getProtein())
                .fat(goal.getFat())
                .date(goal.getDate())
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user1));

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
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

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
        user1.addOrChangeGoal(goal);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user1));
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
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        String username = user1.getUsername();
        LocalDate date = goal.getDate();
        //when then
        assertThatThrownBy(() -> userService.findGoalByDate(date, user1.getUsername()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("특정 날짜 목표 조회 시 목표가 없으면 예외 발생")
    @Test
    void 존재하지_않은_목표_조회_실패() {
        //given
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user1));

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