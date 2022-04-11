package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeResponseDto {
    private int likeCount;
    private String writer;
    private Long postId;

    @Builder
    public LikeResponseDto(int likeCount, String writer, Long postId) {
        this.likeCount = likeCount;
        this.writer = writer;
        this.postId = postId;
    }
}
