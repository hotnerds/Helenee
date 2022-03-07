package com.hotnerds.user.domain.repository;

import com.hotnerds.user.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
