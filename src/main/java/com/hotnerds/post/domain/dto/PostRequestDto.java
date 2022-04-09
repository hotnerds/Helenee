package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class PostRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    private List<@NotBlank String> tagNames;

    @Builder
    public PostRequestDto(String title, String content, List<String> tagNames) {
        this.title = title;
        this.content = content;
        this.tagNames = tagNames;
    }
}
