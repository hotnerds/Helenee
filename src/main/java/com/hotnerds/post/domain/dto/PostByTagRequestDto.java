package com.hotnerds.post.domain.dto;

import lombok.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Getter
public class PostByTagRequestDto {
    private List<String> tagNames;

    Pageable pageable;

    @Builder
    public PostByTagRequestDto(List<String> tagNames, Pageable pageable) {
        this.tagNames = tagNames;
        this.pageable = pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}
