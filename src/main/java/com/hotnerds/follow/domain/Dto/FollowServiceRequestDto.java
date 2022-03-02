package com.hotnerds.follow.domain.Dto;

import com.hotnerds.follow.domain.Follow;
import com.hotnerds.follow.domain.FollowId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowServiceRequestDto {
    private Long followerId;
    private Long followingId;

    @Builder
    public FollowServiceRequestDto(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Follow toEntity() {
        return Follow.builder()
                .followId(FollowId.builder()
                        .followerID(this.followerId)
                        .followingID(this.followingId)
                        .build())
                .build();
    }

    public FollowId toId() {
        return FollowId.builder()
                .followerID(this.followerId)
                .followingID(this.followingId)
                .build();
    }

    public FollowServiceRequestDto reverse() {
        return FollowServiceRequestDto.builder()
                .followerId(this.followingId)
                .followingId(this.followerId)
                .build();
    }

    public static FollowServiceRequestDto Of(Long followerId, Long followingId) {
        return FollowServiceRequestDto.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();
    }
}
