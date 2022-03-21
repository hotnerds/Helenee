package com.hotnerds.post.domain.dto;

import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentUpdateReqDto {
    private Long postId;
    private Long commentId;
    private String content;
}
