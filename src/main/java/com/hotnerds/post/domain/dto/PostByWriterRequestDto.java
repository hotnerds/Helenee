package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@Getter
public class PostByWriterRequestDto {

    private final String writer;

    private int page;

    @JsonProperty("size")
    private int pageSize;

    @Builder
    public PostByWriterRequestDto(String writer, int page, int size) {
        this.writer = writer;
        this.page = page;
        this.pageSize = size;
    }
}
