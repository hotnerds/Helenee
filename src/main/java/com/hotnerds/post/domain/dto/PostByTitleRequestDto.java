package com.hotnerds.post.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class PostByTitleRequestDto {
    @NotBlank
    private String title;

    private Pageable pageable;

    @Builder
    public PostByTitleRequestDto(String title, Pageable pageable) {
        this.title = title;
        this.pageable = pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}
