package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequestDto {
    private String title;
    private String content;
    private List<String> tagNames;

    @Builder
    public PostRequestDto(String title, String content, List<String> tagNames) {
        this.title = title;
        this.content = content;
        this.tagNames = tagNames;
    }
}
