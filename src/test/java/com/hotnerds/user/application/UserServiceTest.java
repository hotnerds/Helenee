package com.hotnerds.user.application;

import com.hotnerds.user.domain.Dto.FollowServiceReqDto;
import com.hotnerds.user.domain.Dto.NewUserReqDto;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import com.hotnerds.user.domain.Follow;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.FollowRelationshipExistsException;
import com.hotnerds.user.exception.FollowRelationshipNotFound;
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
        final User user1 = User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .build();

        final User user2 = User.builder()
                .username("user2")
                .email("user2@gmail.com")
                .build();

        final FollowServiceReqDto reqDto = FollowServiceReqDto.builder()
                .followerId(1L) // given id for user1
                .followedId(2L) // given id for user2
                .build();

        final Follow follow = Follow.builder()
                .follower(user1)
                .followed(user2)
                .build();

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
        final FollowServiceReqDto reqDto = FollowServiceReqDto.builder()
                .followerId(1L) // given id for user1
                .followedId(2L) // given id for user2
                .build();

        final User user1 = User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .build();

        final User user2 = User.builder()
                .username("user2")
                .email("user2@gmail.com")
                .build();

        final Follow follow = Follow.builder()
                .follower(user1)
                .followed(user2)
                .build();

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
        final FollowServiceReqDto reqDto = FollowServiceReqDto.builder()
                .followerId(1L) // given id for user1
                .followedId(2L) // given id for user2
                .build();

        final User user1 = User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .build();

        final User user2 = User.builder()
                .username("user2")
                .email("user2@gmail.com")
                .build();

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
        final FollowServiceReqDto reqDto = FollowServiceReqDto.builder()
                .followerId(1L) // given id for user1
                .followedId(2L) // given id for user2
                .build();

        final User user1 = User.builder()
                .username("user1")
                .email("user1@gmail.com")
                .build();

        final User user2 = User.builder()
                .username("user2")
                .email("user2@gmail.com")
                .build();

        final Follow follow = Follow.builder()
                .follower(user1)
                .followed(user2)
                .build();

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
}