package com.hotnerds.user.application;

import com.hotnerds.user.domain.Dto.NewUserDto;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // given
    private List<NewUserDto> actualNewUserDtoList = Arrays.asList(
            NewUserDto.builder()
                    .username("RetepMil")
                    .email("lkslyj2@naver.com")
                    .build(),
            NewUserDto.builder()
                    .username("PeterLim")
                    .email("lkslyj8@naver.com")
                    .build()
    );

    @Test
    void createNewUser() {
        // mocking
        NewUserDto newUserDto = actualNewUserDtoList.get(0);
        User user = newUserDto.toEntity();
        when(userRepository.save(any())).thenReturn(user);

        // when
        userService.createNewUser(newUserDto);

        // then
        // 실제 DB에 저장되야만 Id가 생성되므로 Mock을 사용해서 테스트가 불가능하다
    }

    @Test
    @DisplayName("유저 전체 조회")
    void getAllUsers() {
        // mocking
        when(userRepository.findAll()).thenReturn(actualNewUserDtoList.stream()
                .map(newUserDto -> newUserDto.toEntity())
                .collect(Collectors.toList()));

        // when
        List<User> gotUserList = userService.getAllUsers();

        // then
        Assertions.assertEquals(2, gotUserList.size());
        Assertions.assertTrue(actualNewUserDtoList.get(0).toEntity()
                .equals(gotUserList.get(0)));
        Assertions.assertTrue(actualNewUserDtoList.get(1).toEntity()
                .equals(gotUserList.get(1)));
    }

    @Test
    void getUserById() {
        // mocking
        User user = actualNewUserDtoList.get(0).toEntity();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        User userFound = userService.getUserById(user.getId());

        // then
        Assertions.assertEquals(user, userFound);
    }

    @Test
    void deleteUserById() {
    }

    @Test
    void updateUser() {
        // mocking
        UserUpdateReqDto userUpdateReqDto = new UserUpdateReqDto("GARAM");
        User user = actualNewUserDtoList.get(0).toEntity();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        userService.updateUser(user.getId(), userUpdateReqDto);

        // then
        // 반환이 void인 createNewUser, deleteUserById, updateUser 같은 경우
        // DB에 적용이 되는지 확인하는 절차가 필요하기 때문에 테스트가 애매함
        // 피드백 필요
    }
}