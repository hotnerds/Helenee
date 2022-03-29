package com.hotnerds.comment.presentation;

import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Dto.*;
import com.hotnerds.common.security.oauth2.annotation.AuthenticatedUser;
import com.hotnerds.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hotnerds.user.presentation.UserController.DEFAULT_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEFAULT_URL)
public class CommentController {
    public static final String DEFAULT_URL = "/comment";
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentCreateReqDto reqDto) {
        commentService.addComment(reqDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@RequestBody CommentDeleteReqDto reqDto, @AuthenticatedUser User user) {
        commentService.deleteComment(reqDto, user.getId());

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateComment(@RequestBody CommentUpdateReqDto reqDto, @AuthenticatedUser User user) {
        commentService.updateComment(reqDto, user.getId());

        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComment(@RequestBody CommentByPostReqDto reqDto) {
        return ResponseEntity.ok(commentService.getComments(reqDto));
    }


}
