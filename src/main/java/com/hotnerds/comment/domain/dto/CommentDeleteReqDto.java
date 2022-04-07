package com.hotnerds.comment.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDeleteReqDto {
    private Long postId;
    private Long commentId;

    @Builder
    public CommentDeleteReqDto(Long postId, Long commentId) {
        this.postId = postId;
        this.commentId = commentId;
    }
}
