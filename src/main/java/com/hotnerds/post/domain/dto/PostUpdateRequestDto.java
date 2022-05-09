package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostUpdateRequestDto {
    @NotNull
    private Long postId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    private List<@NotBlank String> tagNames;

    @Builder
    public PostUpdateRequestDto(Long postId, String title, String content, List<String> tagNames) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.tagNames = tagNames;
    }
}
