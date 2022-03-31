package com.hotnerds.comment.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@Getter
@Builder
public class CommentByPostReqDto {

    private final Long postId;
    private final PageRequest pageable;

}
