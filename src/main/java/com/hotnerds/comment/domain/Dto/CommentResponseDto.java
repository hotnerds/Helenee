package com.hotnerds.comment.domain.Dto;

import com.hotnerds.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentResponseDto {
    private Long commentId;
    private Long userId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;

    public static CommentResponseDto Of(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .userId(comment.getWriter().getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
