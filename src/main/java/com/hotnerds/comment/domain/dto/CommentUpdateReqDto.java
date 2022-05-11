package com.hotnerds.comment.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentUpdateReqDto {
    private Long postId;
    private Long commentId;
    private String content;

    @Builder
    public CommentUpdateReqDto(Long postId, Long commentId, String content) {
        this.postId = postId;
        this.commentId = commentId;
        this.content = content;
    }
}
