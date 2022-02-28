package com.hotnerds.user.domain.repository;

import com.hotnerds.user.domain.Dto.NewUserReqDto;
import com.hotnerds.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("DB에 유저 정보 저장")
    void saveUser() {
        // given
        User userReq = User.builder()
                .username("Peter")
                .email("lkslyj2@naver.com")
                .build();

        // when
        User userSaved = userRepository.save(userReq);

        // then
        Assertions.assertThat(userReq).isSameAs(userSaved);
        Assertions.assertThat(userReq.getUsername()).isEqualTo(userSaved.getUsername());
        Assertions.assertThat(userSaved).isNotNull();
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("username 값으로 유저 검색")
    void findByUsername() {
        // given
        User user = User.builder()
                .username("Peter")
                .email("lkslyj2@naver.com")
                .build();
        userRepository.save(user);

        // when
        User userFound = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(user.getUsername() + "is invald username"));
        Optional<User> userWrongUsername = userRepository.findByUsername("Wrong Username"); // fail case condition

        // then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        Assertions.assertThat(userFound).isSameAs(userFound);
        Assertions.assertThat(userWrongUsername.isEmpty()).isEqualTo(true); // fail condition
    }

    @Test
    @DisplayName("email 값으로 유저 검색")
    void findByEmail() {
        // given
        User user = User.builder()
                .username("Peter")
                .email("lkslyj2@naver.com")
                .build();
        userRepository.save(user);

        // when
        User userFound = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(user.getEmail() + "is invald email"));
        Optional<User> userWrongEmail = userRepository.findByUsername("Wrong Email"); // fail case condition

        // then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        Assertions.assertThat(userFound).isSameAs(userFound);
        Assertions.assertThat(userWrongEmail.isEmpty()).isEqualTo(true); // fail condition
    }

    @Test
    @DisplayName("username 또는 email 값으로 유저 검색")
    void findByUsernameOrEmail() {
        // given
        User user = User.builder()
                .username("Peter")
                .email("lkslyj2@naver.com")
                .build();
        userRepository.save(user);

        // when
        User userFoundOnlyCorrectUsername = userRepository.findByUsernameOrEmail(user.getUsername(), "Wrong Key")
                .orElseThrow(() -> new IllegalArgumentException("Invalid search parameters"));
        User userFoundOnlyCorrectEmail = userRepository.findByUsernameOrEmail("Wrong Key", user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid search parameters"));
        User userFoundCorrectUsernameAndEmail = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid search parameters"));
        Optional<User> userFoundWrongUsernameAndEmail = userRepository.findByUsernameOrEmail("Wrong Key", "Wrong Key");

        // then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        Assertions.assertThat(userFoundOnlyCorrectUsername).isEqualTo(user);
        Assertions.assertThat(userFoundOnlyCorrectEmail).isEqualTo(user);
        Assertions.assertThat(userFoundCorrectUsernameAndEmail).isEqualTo(user);
        Assertions.assertThat(userFoundWrongUsernameAndEmail.isEmpty()).isTrue();
    }
}