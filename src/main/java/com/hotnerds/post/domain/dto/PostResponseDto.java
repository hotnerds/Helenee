package com.hotnerds.post.domain.dto;

import com.hotnerds.post.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
    private int likeCount;
    private List<String> tagNames;


    @Builder
    public PostResponseDto(Long postId, String title, String content, String writer, LocalDateTime createdAt, int likeCount, List<String> tagNames) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.tagNames = tagNames;
    }

    @Builder
    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getWriter().getUsername())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikeCount())
                .tagNames(post.getTagNames())
                .build();
    }
}
