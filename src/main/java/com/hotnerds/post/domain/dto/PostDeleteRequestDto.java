package com.hotnerds.post.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDeleteRequestDto {

    private String username;
    private Long postId;

    @Builder
    public PostDeleteRequestDto(String username, Long postId) {
        this.username = username;
        this.postId = postId;
    }
}
