package com.hotnerds.followers.domain.Dto;

import com.hotnerds.followers.domain.Follower;
import com.hotnerds.followers.domain.FollowerId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowerServiceRequestDto {
    private Long followerId;
    private Long followingId;

    @Builder
    public FollowerServiceRequestDto(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Follower toEntity() {
        return Follower.builder()
                .followerId(FollowerId.builder()
                        .followerID(this.followerId)
                        .followingID(this.followingId)
                        .build())
                .build();
    }

    public FollowerId toId() {
        return FollowerId.builder()
                .followerID(this.followerId)
                .followingID(this.followingId)
                .build();
    }

    public FollowerServiceRequestDto reverse() {
        return FollowerServiceRequestDto.builder()
                .followerId(this.followingId)
                .followingId(this.followerId)
                .build();
    }

    public static FollowerServiceRequestDto Of(Long followerId, Long followingId) {
        return FollowerServiceRequestDto.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();
    }
}
