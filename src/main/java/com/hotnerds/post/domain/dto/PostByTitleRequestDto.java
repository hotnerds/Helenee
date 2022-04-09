package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class PostByTitleRequestDto {
    @NotBlank
    private String title;

    private PageInfo pageInfo;

    @Builder
    public PostByTitleRequestDto(String title, int page, int size) {
        this.title = title;
        this.pageInfo = PageInfo.builder()
                .page(page)
                .size(size)
                .build();
    }
}
