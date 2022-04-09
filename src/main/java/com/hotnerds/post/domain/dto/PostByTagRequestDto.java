package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostByTagRequestDto {
    private List<String> tagNames;

    private int page;

    @JsonProperty("size")
    private int pageSize;

    public PostByTagRequestDto(List<String> tagNames) {
        this.page = 0;
        this.pageSize = 10;
        this.tagNames = tagNames;
    }

    @Builder
    public PostByTagRequestDto(List<String> tagNames, int page, int size) {
        this.tagNames = tagNames;
        this.page = page;
        this.pageSize = size;
    }
}
