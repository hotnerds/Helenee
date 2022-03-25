package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Getter
public class PostByTagRequestDto {
    private List<String> tagNames;
    private PageRequest pageable;

    @Builder
    public PostByTagRequestDto(List<String> tagNames, PageRequest pageable) {
        this.tagNames = tagNames;
        this.pageable = pageable;
    }
}
