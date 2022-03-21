package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDeleteReqDto {
    private Long userId;
    private Long postId;
    private Long commentId;
}
