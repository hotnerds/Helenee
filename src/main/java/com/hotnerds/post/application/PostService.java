package com.hotnerds.post.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.*;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.tag.application.TagService;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagService tagService;

    @Transactional
    public Long write(PostRequestDto requestDto) {
        return postRepository.save(createPost(requestDto)).getId();
    }

    public List<PostResponseDto> searchAll(Pageable pageable) {
        return postRepository.findAllPosts(pageable)
                .stream()
                .map(PostResponseDto::of)
                .collect(toList());
    }

    public List<PostResponseDto> searchByTitle(PostByTitleRequestDto requestDto) {

        return postRepository.findAllByTitle(requestDto.getTitle(), requestDto.getPageable()).stream()
                .map(PostResponseDto::of)
                .collect(toList());
    }

    public List<PostResponseDto> searchByWriter(PostByWriterRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getWriter())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        return postRepository.findAllByUser(user, requestDto.getPageable()).stream()
                .map(PostResponseDto::of)
                .collect(toList());
    }

    public List<PostResponseDto> searchByTagNames(PostByTagRequestDto requestDto) {
        requestDto.getTagNames()
                .forEach(Tag::validateTagName);

        return postRepository.findAllByTagNames(requestDto.getTagNames(), requestDto.getPageable()).stream()
                .map(PostResponseDto::of)
                .collect(toList());
    }

    private Post createPost(PostRequestDto postRequestDto) {
        User user = userRepository.findByUsername(postRequestDto.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Post post = new Post(
                postRequestDto.getTitle(),
                postRequestDto.getContent(),
                user);

        postRequestDto.getTagNames().stream()
                .map(tagService::findOrCreateTag)
                .forEach(post::addTag);

        return post;
    }

    @Transactional
    public void delete(PostDeleteRequestDto requestDto) {

        userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        postRepository.deleteById(requestDto.getPostId());
    }

    @Transactional
    public void update(PostUpdateRequestDto updateRequestDto) {
        Post post = postRepository.findById(updateRequestDto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        User user = userRepository.findByUsername(updateRequestDto.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if(!post.isWriter(user)) {
            throw new BusinessException(ErrorCode.POST_WRITER_NOT_MATCH_EXCEPTION);
        }

        post.updateTitleAndContent(updateRequestDto.getTitle(), updateRequestDto.getContent());

        post.clearTag();

        updateRequestDto.getTagNames().stream()
                .map(tagService::findOrCreateTag)
                .forEach(post::addTag);
    }

    @Transactional
    public LikeResponseDto like(String username, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        post.like(user);

        return LikeResponseDto.builder()
                .likeCount(post.getLikeCount())
                .username(user.getUsername())
                .postId(post.getId())
                .build();
    }

    @Transactional
    public LikeResponseDto unlike(String username, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        post.unlike(user);

        return LikeResponseDto.builder()
                .likeCount(post.getLikeCount())
                .username(user.getUsername())
                .postId(post.getId())
                .build();
    }
}
