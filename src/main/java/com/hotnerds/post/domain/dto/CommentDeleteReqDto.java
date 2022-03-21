package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class CommentDeleteReqDto {
    private Long postId;
    private Long commentId;
    private LocalDateTime createAt;
}
