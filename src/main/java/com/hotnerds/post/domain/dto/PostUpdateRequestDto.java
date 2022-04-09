package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateRequestDto {
    private Long postId;
    private String title;
    private String content;
    private List<String> tagNames;

    @Builder
    public PostUpdateRequestDto(Long postId, String title, String content, List<String> tagNames) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.tagNames = tagNames;
    }
}
