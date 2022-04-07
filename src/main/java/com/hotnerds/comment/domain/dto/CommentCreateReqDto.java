package com.hotnerds.comment.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateReqDto {
    private Long userId;
    private Long postId;
    private String content;

    @Builder
    public CommentCreateReqDto(Long userId, Long postId, String content) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }
}
