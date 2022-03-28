package com.hotnerds.comment.domain.Dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;

@Getter
@Builder
public class CommentByPostReqDto {

    private final Long postId;
    private final PageRequest pageable;

}
