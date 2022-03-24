package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateRequestDto {
    private Long postId;
    private String username;
    private String title;
    private String content;
    private List<String> tagNames;

    @Builder
    public PostUpdateRequestDto(Long postId, String username, String title, String content, List<String> tagNames) {
        this.postId = postId;
        this.username = username;
        this.title = title;
        this.content = content;
        this.tagNames = tagNames;
    }
}
