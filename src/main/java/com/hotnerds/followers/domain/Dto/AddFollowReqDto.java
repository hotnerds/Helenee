package com.hotnerds.user.domain.Dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddFollowReqDto {
    private Long followerId;
    private Long followingId;

    @Builder
    public AddFollowReqDto(Long followerId, Long followingId) {
        this.followerId = followerId; this.followingId = followingId;
    }
}
