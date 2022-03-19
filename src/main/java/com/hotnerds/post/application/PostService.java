package com.hotnerds.post.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.*;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void write(PostRequestDto requestDto) {
        Post savedPost = postRepository.save(createPost(requestDto));
    }

    public List<PostResponseDto> searchByTitle(String title) {
        List<Post> findPosts = postRepository.findAllByTitle(title);

        return findPosts.stream()
                .map(PostResponseDto::of)
                .collect(toList());
    }

    public List<PostResponseDto> searchByWriter(PostByUserRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        return postRepository.findAllByUser(user, requestDto.getPageable())
                .stream()
                .map(PostResponseDto::of)
                .collect(toList());
    }

    private Post createPost(PostRequestDto postRequestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(postRequestDto.getUsername());

        User user = optionalUser.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        return Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .writer(user)
                .build();

    }

    public void deletePost(PostDeleteRequestDto requestDto) {

        userRepository.findByUsername(requestDto.getUsername()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        postRepository.findById(requestDto.getPostId()).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        postRepository.deleteById(requestDto.getPostId());
    }

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
