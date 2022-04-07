package com.hotnerds.comment.domain.dto;

import com.hotnerds.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private Long userId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    public CommentResponseDto(Long commentId, Long userId, Long postId, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CommentResponseDto of(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .userId(comment.getWriter().getId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
