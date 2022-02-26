package com.hotnerds.followers.domain.repository;

import com.hotnerds.followers.domain.Follower;
import com.hotnerds.followers.domain.FollowerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
}
