package com.hotnerds.comment.domain.Dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
