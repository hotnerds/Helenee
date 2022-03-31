package com.hotnerds.comment.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentUpdateReqDto {
    private Long postId;
    private Long commentId;
    private String content;
}
