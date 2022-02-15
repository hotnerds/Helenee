package com.hotnerds.user.domain.repository;

import com.hotnerds.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

}
