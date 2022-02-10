package com.hotnerds.user.domain.repository;

import com.hotnerds.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
