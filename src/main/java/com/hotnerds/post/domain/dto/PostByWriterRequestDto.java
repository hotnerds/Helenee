package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class PostByWriterRequestDto {

    @NotBlank
    private final String writer;

    private Pageable pageable;

    @Builder
    public PostByWriterRequestDto(String writer, Pageable pageable) {
        this.writer = writer;
        this.pageable = pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}
