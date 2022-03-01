package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@Getter
public class PostByUserRequestDto {

    private final String username;
    private final PageRequest pageable;

    @Builder
    public PostByUserRequestDto(String username, PageRequest pageable) {
        this.username = username;
        this.pageable = pageable;
    }
}
