package com.hotnerds.follow.domain.repository;

import com.hotnerds.follow.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
