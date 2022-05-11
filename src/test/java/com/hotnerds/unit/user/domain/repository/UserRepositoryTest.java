package com.hotnerds.unit.user.domain.repository;

import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(userReq).isSameAs(userSaved);
        assertThat(userReq.getUsername()).isEqualTo(userSaved.getUsername());
        assertThat(userSaved).isNotNull();
        assertThat(userRepository.count()).isEqualTo(1);
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
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userWrongUsername).isEmpty(); // fail condition
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
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userWrongEmail).isEmpty(); // fail condition
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
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userFoundOnlyCorrectUsername).isEqualTo(user);
        assertThat(userFoundOnlyCorrectEmail).isEqualTo(user);
        assertThat(userFoundCorrectUsernameAndEmail).isEqualTo(user);
        assertThat(userFoundWrongUsernameAndEmail).isEmpty();
    }

}