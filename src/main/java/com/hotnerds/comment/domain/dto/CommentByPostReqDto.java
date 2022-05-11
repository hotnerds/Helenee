package com.hotnerds.comment.domain.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentByPostReqDto {

    private Long postId;
    private PageRequest pageable;

    @Builder
    public CommentByPostReqDto(Long postId, PageRequest pageable) {
        this.postId = postId;
        this.pageable = pageable;
    }
}
