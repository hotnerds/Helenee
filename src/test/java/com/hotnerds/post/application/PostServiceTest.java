package com.hotnerds.post.application;

import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.PostRequestDto;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    PostService postService;

    User user;

    Post post;

    @DisplayName("게시글 등록")
    @Test
    void 게시글_등록() {
        //given

        User user = User.builder()
                .username("name")
                .email("email")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(user)
                .build();

        PostRequestDto requestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .username("username")
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        //when
        postService.write(requestDto);

        //then
        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(postRepository, times(1)).save(any(Post.class));
    }





}
