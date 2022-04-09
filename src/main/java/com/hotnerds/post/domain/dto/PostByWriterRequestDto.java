package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@Getter
public class PostByWriterRequestDto {

    private final String writer;
    private final PageRequest pageable;

    @Builder
    public PostByWriterRequestDto(String writer, PageRequest pageable) {
        this.writer = writer;
        this.pageable = pageable;
    }
}
