package com.hotnerds.post.domain.repository;

import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("게시글 등록 성공")
    public void 게시글_등록_성공() {
        //given
        User user = User.builder()
                .username("username")
                .email("email")
                .build();

        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .title("temp title")
                .content("body")
                .writer(savedUser)
                .build();

        //when
        Post savedPost = postRepository.save(post);

        //then
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getTitle()).isEqualTo("temp title");
        assertThat(savedPost.getContent()).isEqualTo("body");
        assertThat(savedPost.getWriter().getId()).isNotNull();

    }

    @Test
    @DisplayName("게시글 제목으로 조회")
    public void 게시글_제목으로_조회_성공() {
        //given
        User user = User.builder()
                .username("username")
                .email("email")
                .build();

        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .title("temp")
                .content("content")
                .writer(savedUser)
                .build();

        postRepository.save(post);
        //when

        List<Post> findPosts = postRepository.findByTitle("temp");

        Post findPost = findPosts.get(0);

        //then
        assertThat(findPost.getId()).isNotNull();
        assertThat(findPost.getContent()).isEqualTo("content");
        assertThat(findPost.getWriter()).isNotNull();
    }

}