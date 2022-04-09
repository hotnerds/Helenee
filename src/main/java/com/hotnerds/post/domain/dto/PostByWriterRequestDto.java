package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Range;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class PostByWriterRequestDto {

    @NotBlank
    private final String writer;

    @Min(0)
    private int page;

    @JsonProperty("size")
    @Min(1)
    private int pageSize;

    @Builder
    public PostByWriterRequestDto(String writer, int page, int size) {
        this.writer = writer;
        this.page = page;
        this.pageSize = size;
    }
}
