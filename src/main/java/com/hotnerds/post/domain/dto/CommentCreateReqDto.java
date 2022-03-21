package com.hotnerds.post.domain.dto;

import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
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
