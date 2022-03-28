package com.hotnerds.comment.domain.Dto;

import com.hotnerds.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CommentResponseDto {
    List<Comment> commentList;
}
