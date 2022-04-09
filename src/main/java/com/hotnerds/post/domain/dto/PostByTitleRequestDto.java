package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostByTitleRequestDto {
    private String title;

    private int page;

    @JsonProperty("size")
    private int pageSize;

    @Builder
    public PostByTitleRequestDto(String title, int page, int size) {
        this.title = title;
        this.page = page;
        this.pageSize = size;
    }
}
