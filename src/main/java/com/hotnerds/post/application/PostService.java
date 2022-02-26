package com.hotnerds.post.application;

import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.PostRequestDto;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void write(PostRequestDto requestDto) {
        Post savedPost = postRepository.save(createPost(requestDto));
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
}
