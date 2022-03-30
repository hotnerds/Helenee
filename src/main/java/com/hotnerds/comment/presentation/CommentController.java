package com.hotnerds.comment.presentation;

import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.Dto.*;
import com.hotnerds.common.security.oauth2.annotation.AuthenticatedUser;
import com.hotnerds.user.domain.User;
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
    public ResponseEntity<?> createComment(@RequestBody CommentCreateReqDto reqDto) {
        Comment newComment = commentService.addComment(reqDto);
        CommentResponseDto responseDto = CommentResponseDto.Of(newComment);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable("post_id") Long post_id, @PathVariable("comment_id") Long comment_id, @AuthenticatedUser User user) {
        CommentDeleteReqDto reqDto = new CommentDeleteReqDto(post_id, comment_id);
        commentService.deleteComment(reqDto, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<?> updateComment(@RequestBody CommentUpdateReqDto reqDto, @AuthenticatedUser User user) {
        Comment updatedComment = commentService.updateComment(reqDto, user.getId());
        CommentResponseDto responseDto = CommentResponseDto.Of(updatedComment);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComment(@PathVariable("post_id") Long post_id, @RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(commentService.getComments(CommentByPostReqDto.builder()
                .postId(post_id)
                .pageable(PageRequest.of(page, size))
                .build()));
    }

}
