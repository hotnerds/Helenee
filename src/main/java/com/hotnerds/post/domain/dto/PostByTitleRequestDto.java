package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class PostByTitleRequestDto {
    private String title;
    private Pageable pageable;

    @Builder

    public PostByTitleRequestDto(String title, Pageable pageable) {
        this.title = title;
        this.pageable = pageable;
    }
}
