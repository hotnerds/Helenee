package com.hotnerds.user.application;

import com.hotnerds.user.domain.Dto.NewUserDto;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserExistsException;
import com.hotnerds.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createNewUser(NewUserDto newUserDto) {
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(newUserDto.getUsername(), newUserDto.getEmail());
        if (!optionalUser.isEmpty()) {
            throw new UserExistsException("동일한 정보를 가진 유저가 이미 존재합니다");
        }

        userRepository.save(newUserDto.toEntity());
    }

    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User ID " + userId + "가 존재하지 않습니다"));

        return user;
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    public void updateUser(Long userId, UserUpdateReqDto userUpdateReqDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException("User ID " + userId + "가 존재하지 않습니다"));

        user.updateUser(userUpdateReqDto);
    }

}
