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
        User userReq = new NewUserReqDto("Peter", "lkslyj2@naver.com").toEntity();

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
        User user1 = new NewUserReqDto("PeterLim", "lkslyj2@naver.com").toEntity();
        userRepository.save(user1);

        // when
        User userFound = userRepository.findByUsername(user1.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(user1.getUsername() + "is invald username"));

        // then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        Assertions.assertThat(userFound).isSameAs(userFound);
    }

    @Test
    @DisplayName("email 값으로 유저 검색")
    void findByEmail() {
        // given
        User user1 = new NewUserReqDto("PeterLim", "lkslyj2@naver.com").toEntity();
        userRepository.save(user1);

        // when
        User userFound = userRepository.findByEmail(user1.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(user1.getEmail() + "is invald email"));

        // then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        Assertions.assertThat(userFound).isSameAs(userFound);
    }

    @Test
    @DisplayName("username 또는 email 값으로 유저 검색")
    void findByUsernameOrEmail() {
        // given
        User user = new NewUserReqDto("PeterLim", "lkslyj2@naver.com").toEntity();
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