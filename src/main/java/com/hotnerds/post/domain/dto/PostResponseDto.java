package com.hotnerds.post.domain.dto;

import com.hotnerds.post.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;


    @Builder
    public PostResponseDto(String title, String content, String username, LocalDateTime createdAt) {
        this.title = title;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
    }

    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .username(post.getWriter().getUsername())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
