package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
public class PostByTagRequestDto {
    @NotEmpty
    private List<@NotBlank String> tagNames;

    PageInfo pageInfo;

    @Builder
    public PostByTagRequestDto(List<String> tagNames, int page, int size) {
        this.tagNames = tagNames;
        this.pageInfo = PageInfo.builder()
                .page(page)
                .size(size)
                .build();
    }
}
