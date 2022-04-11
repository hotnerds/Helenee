package com.hotnerds.post.presentation;


import com.hotnerds.common.security.oauth2.annotation.Authenticated;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.post.application.PostService;
import com.hotnerds.post.domain.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> createPost(@RequestBody PostRequestDto requestDto, @Authenticated AuthenticatedUser authUser) {
        Long postId = postService.write(requestDto, authUser);
        return ResponseEntity.created(URI.create(POST_API_URI + "/" + postId)).build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> searchAllPosts(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.searchAll(pageable));
    }

    @GetMapping(params = {"title"})
    public ResponseEntity<List<PostResponseDto>> searchPostsByTitle(@Valid PostByTitleRequestDto requestDto,
                                                                    @PageableDefault Pageable pageable) {
        requestDto.setPageable(pageable);
        return ResponseEntity.ok(postService.searchByTitle(requestDto));
    }

    @GetMapping(params = {"writer"})
    public ResponseEntity<List<PostResponseDto>> searchPostsByWriter(@Valid PostByWriterRequestDto requestDto,
                                                                     @PageableDefault Pageable pageable) {
        requestDto.setPageable(pageable);
        return ResponseEntity.ok(postService.searchByWriter(requestDto));
    }

    @GetMapping(params = {"tagNames"})
    public ResponseEntity<List<PostResponseDto>> searchPostsByTagNames(@Valid PostByTagRequestDto requestDto,
                                                                       @PageableDefault Pageable pageable) {
        requestDto.setPageable(pageable);
        return ResponseEntity.ok(postService.searchByTagNames(requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@NotNull @PathVariable("id") Long postId, @Authenticated AuthenticatedUser authUser) {
        postService.delete(postId, authUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePost(@Valid @RequestBody PostUpdateRequestDto requestDto, @Authenticated AuthenticatedUser authUser) {
        postService.update(requestDto, authUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<LikeResponseDto> likePost(@NotNull @PathVariable("id") Long postId, @Authenticated AuthenticatedUser authUser) {
        return ResponseEntity.ok(postService.like(postId, authUser));
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<LikeResponseDto> unlikePost(@NotNull @PathVariable("id") Long postId, @Authenticated AuthenticatedUser authUser) {
        return ResponseEntity.ok(postService.unlike(postId, authUser));
    }
}
