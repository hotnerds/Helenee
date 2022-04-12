package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import javax.validation.constraints.NotBlank;

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
