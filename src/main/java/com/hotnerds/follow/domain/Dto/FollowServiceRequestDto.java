package com.hotnerds.follow.domain.Dto;

import com.hotnerds.follow.domain.Follow;
import com.hotnerds.user.domain.User;
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

    public FollowServiceRequestDto reverse() {
        return FollowServiceRequestDto.builder()
                .followerId(followingId)
                .followingId(followerId)
                .build();
    }

    public static FollowServiceRequestDto Of(User followerUser, User followingUser) {
        return FollowServiceRequestDto.builder()
                .followerId(followerUser.getId())
                .followingId(followingUser.getId())
                .build();
    }
}
