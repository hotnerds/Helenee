package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import javax.validation.constraints.NotBlank;

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
