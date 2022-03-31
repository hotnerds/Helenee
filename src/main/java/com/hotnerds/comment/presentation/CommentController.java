package com.hotnerds.comment.presentation;

import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.dto.*;
import com.hotnerds.common.security.oauth2.annotation.Authenticated;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hotnerds.comment.presentation.CommentController.DEFAULT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class CommentController {
    public static final String DEFAULT_URL = "/api/posts/{post_id}/comments";
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentCreateReqDto reqDto) {
        Comment newComment = commentService.addComment(reqDto);
        CommentResponseDto responseDto = CommentResponseDto.of(newComment);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId, @Authenticated AuthenticatedUser user) {
        CommentDeleteReqDto reqDto = new CommentDeleteReqDto(postId, commentId);
        commentService.deleteComment(reqDto, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<CommentResponseDto> updateComment(@RequestBody CommentUpdateReqDto reqDto, @Authenticated AuthenticatedUser user) {
        Comment updatedComment = commentService.updateComment(reqDto, user.getId());
        CommentResponseDto responseDto = CommentResponseDto.of(updatedComment);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComment(@PathVariable("post_id") Long postId, @RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(commentService.getComments(CommentByPostReqDto.builder()
                .postId(postId)
                .pageable(PageRequest.of(page, size))
                .build()));
    }

}
