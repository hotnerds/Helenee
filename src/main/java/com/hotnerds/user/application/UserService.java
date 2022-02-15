package com.hotnerds.user.application;

import com.hotnerds.user.domain.DTO.NewUserDto;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserExistsException;
import com.hotnerds.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
        if (userRepository.findByUsername(newUserDto.getUsername()) != null
                || userRepository.findByEmail(newUserDto.getEmail()) != null) {
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

}
