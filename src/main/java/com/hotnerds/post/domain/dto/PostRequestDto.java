package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
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
