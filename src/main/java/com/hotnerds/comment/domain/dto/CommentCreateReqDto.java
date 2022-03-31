package com.hotnerds.comment.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentCreateReqDto {
    private Long userId;
    private Long postId;
    private String content;
}
