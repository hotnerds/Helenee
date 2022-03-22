package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeResponseDto {
    private int likeCount;
    private String username;
    private Long postId;

    @Builder
    public LikeResponseDto(int likeCount, String username, Long postId) {
        this.likeCount = likeCount;
        this.username = username;
        this.postId = postId;
    }
}
