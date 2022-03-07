package com.hotnerds.post.application;

import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.PostByUserRequestDto;
import com.hotnerds.post.domain.dto.PostDeleteRequestDto;
import com.hotnerds.post.domain.dto.PostRequestDto;
import com.hotnerds.post.domain.dto.PostResponseDto;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.post.exception.PostNotFoundException;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserNotFoundException;
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
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(UserNotFoundException::new);

        return postRepository.findAllByUser(user, requestDto.getPageable())
                .stream()
                .map(PostResponseDto::of)
                .collect(toList());
    }

    private Post createPost(PostRequestDto postRequestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(postRequestDto.getUsername());

        User user = optionalUser.orElseThrow(UserNotFoundException::new);

        return Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .writer(user)
                .build();

    }

    public void deletePost(PostDeleteRequestDto requestDto) {

        userRepository.findByUsername(requestDto.getUsername()).orElseThrow(UserNotFoundException::new);

        postRepository.findById(requestDto.getPostId()).orElseThrow(PostNotFoundException::new);

        postRepository.deleteById(requestDto.getPostId());
    }
}