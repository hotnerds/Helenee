package com.hotnerds.followers.domain.repository;

import com.hotnerds.followers.domain.Follower;
import com.hotnerds.followers.domain.FollowerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    Optional<Follower> FindByFollowerIdAndFollowingId(Long followerId, Long followingId);
    List<Follower> FindByFollowerId(Long followerId);
    List<Follower> FindByFollowingId(Long followingId);
}
