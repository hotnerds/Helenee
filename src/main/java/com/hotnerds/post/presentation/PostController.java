package com.hotnerds.post.presentation;


import com.hotnerds.common.security.oauth2.annotation.Authenticated;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.post.application.PostService;
import com.hotnerds.post.domain.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

import static com.hotnerds.post.presentation.PostController.*;

@RestController
@RequestMapping(POST_API_URI)
@RequiredArgsConstructor
@Slf4j
public class PostController {
    public static final String POST_API_URI = "/api/posts";

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> createPosts(PostRequestDto requestDto, @Authenticated AuthenticatedUser authUser) {
        Long postId = postService.write(requestDto, authUser);
        return ResponseEntity.created(URI.create(POST_API_URI)).build();
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<List<PostResponseDto>> searchAllPosts(@Valid PageInfo pageInfo) {
        return ResponseEntity.ok(postService.searchAll(pageInfo));
    }

    @GetMapping(params = {"page", "size", "title"})
    public ResponseEntity<List<PostResponseDto>> searchPostsByTitle(@Valid PostByTitleRequestDto requestDto) {
        return ResponseEntity.ok(postService.searchByTitle(requestDto));
    }

    @GetMapping(params = {"page", "size", "writer"})
    public ResponseEntity<List<PostResponseDto>> searchPostsByWriter(@Valid PostByWriterRequestDto requestDto) {
        return ResponseEntity.ok(postService.searchByWriter(requestDto));
    }

    @GetMapping(params = {"page", "size", "tagNames"})
    public ResponseEntity<List<PostResponseDto>> searchPostsByTagNames(@Valid PostByTagRequestDto requestDto) {
        return ResponseEntity.ok(postService.searchByTagNames(requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosts(@Valid @PathVariable("id") Long postId, @Authenticated AuthenticatedUser authUser) {
        postService.delete(postId, authUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePosts(@Valid PostUpdateRequestDto requestDto, @Authenticated AuthenticatedUser authUser) {
        postService.update(requestDto, authUser);
        return ResponseEntity.noContent().build();
    }


}
