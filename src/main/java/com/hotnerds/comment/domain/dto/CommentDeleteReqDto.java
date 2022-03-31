package com.hotnerds.comment.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentDeleteReqDto {
    private Long postId;
    private Long commentId;

    @Builder
    public CommentDeleteReqDto(Long postId, Long commentId) {
        this.postId = postId;
        this.commentId = commentId;
    }
}
