package com.hotnerds.user.application;

import com.hotnerds.user.domain.Dto.FollowServiceReqDto;
import com.hotnerds.user.domain.Dto.NewUserReqDto;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.FollowedList;
import com.hotnerds.user.domain.FollowerList;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.FollowRelationshipExistsException;
import com.hotnerds.user.exception.FollowRelationshipNotFound;
import com.hotnerds.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private List<NewUserReqDto> actualNewUserReqDtoList = Arrays.asList(
            NewUserReqDto.builder()
                    .username("RetepMil")
                    .email("lkslyj2@naver.com")
                    .build(),
            NewUserReqDto.builder()
                    .username("PeterLim")
                    .email("lkslyj8@naver.com")
                    .build()
    );

    private User user1;
    private User user2;
    private Follow follow;
    private FollowServiceReqDto reqDto;

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
    }

    @Test
    void createNewUser() {
        // mocking
        NewUserReqDto newUserReqDto = actualNewUserReqDtoList.get(0);
        User user = newUserReqDto.toEntity();
        when(userRepository.save(any())).thenReturn(user);

        // when
        userService.createNewUser(newUserReqDto);

        // then
        // 실제 DB에 저장되야만 Id가 생성되므로 Mock을 사용해서 테스트가 불가능하다
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

    @Test
    void deleteUserById() {
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

    @Test
    @DisplayName("새로운 팔로우 관계를 생성할때 같은 관계가 존재하면 예외 발생")
    void 팔로우_중복_확인() {
        // given
        user1.getFollowedList().add(follow);
        user2.getFollowerList().add(follow);

        when(userRepository.findById(reqDto.getFollowerId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(reqDto.getFollowedId())).thenReturn(Optional.of(user2));

        // when then
        assertThrows(FollowRelationshipExistsException.class, () -> userService.createFollow(reqDto));
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
    @DisplayName("없는 정보의 팔로우 검색 시도 시 예외발생")
    public void 없는_팔로우_관계_검색() {
        // given
        when(userRepository.findById(reqDto.getFollowerId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(reqDto.getFollowedId())).thenReturn(Optional.of(user2));

        // when then
        assertThrows(FollowRelationshipNotFound.class, () -> userService.getOneFollow(reqDto));
        verify(userRepository, times(1)).findById(reqDto.getFollowerId());
        verify(userRepository, times(1)).findById(reqDto.getFollowedId());
    }

    @Test
    @DisplayName("팔로우 관계를 팔로워 ID와 피팔로워 ID로 검색 가능")
    public void 팔로우_관계_검색() {
        // given
        user1.getFollowedList().add(follow);
        user2.getFollowerList().add(follow);

        when(userRepository.findById(reqDto.getFollowerId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(reqDto.getFollowedId())).thenReturn(Optional.of(user2));

        // when
        Follow searchResult = userService.getOneFollow(reqDto);

        // then
        assertEquals(follow.getFollower(), searchResult.getFollower());
        assertEquals(follow.getFollowed(), searchResult.getFollowed());
        verify(userRepository, times(1)).findById(reqDto.getFollowerId());
        verify(userRepository, times(1)).findById(reqDto.getFollowedId());
    }

    @Test
    @DisplayName("존재하지 않는 id를 가진 유저를 팔로잉하는 모든 유저들의 id를 검색하면 예외발생")
    public void 유저_팔로워_리스트_오류() {
        // when then
        assertThrows(UserNotFoundException.class, () -> userService.getUserFollowers(999L));
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

        List<Long> expectedList = Arrays.asList(1L, 3L);

        // when
        List<Long> userIdList = userService.getUserFollowers(2L); // 2 is user id for user2

        // then
        assertEquals(expectedList.get(0), userIdList.get(0));
        assertEquals(expectedList.get(1), userIdList.get(1));
        verify(userRepository).findById(2L);
    }

    @Test
    @DisplayName("존재하지 않는 id를 가진 유저가 팔로잉하는 모든 유저들의 id를 검색하면 예외발생")
    public void 유저_팔로잉_리스트_오류() {
        // when then
        assertThrows(UserNotFoundException.class, () -> userService.getUserFollowings(999L));
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

        List<Long> expectedList = Arrays.asList(1L, 3L);

        // when
        List<Long> userIdList = userService.getUserFollowings(2L); // 2 is user id for user2

        // then
        assertEquals(expectedList.get(0), userIdList.get(0));
        assertEquals(expectedList.get(1), userIdList.get(1));
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
        boolean expectMutualTrue = userService.isMutualFollowExist(user1, user2);
        boolean expectMutualFalse = userService.isMutualFollowExist(user2, user3); // user 3 follows user 2, but not vice versa

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
        assertThrows(FollowRelationshipNotFound.class, () -> userService.deleteFollow(reqDto));
    }

    @Test
    @DisplayName("팔로우 관계 취소 기능이 되어야 한다")
    public void 유저_팔로우_취소() {
        // given
        user1.getFollowedList().add(follow);
        user2.getFollowedList().add(follow);
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
}