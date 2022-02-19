package com.hotnerds.post.domain.repository;

import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
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

        List<Post> findPosts = postRepository.findAllByTitle("temp");

        Post findPost = findPosts.get(0);

        //then
        assertThat(findPost.getId()).isNotNull();
        assertThat(findPost.getContent()).isEqualTo("content");
        assertThat(findPost.getWriter()).isNotNull();
    }

    @Test
    @DisplayName("특정 시간 이후에 생성된 게시글 조회")
    public void 특정시간_이후에_생성된_게시글_조회() {
        //given
        User user = User.builder()
                .username("username")
                .email("email")
                .build();

        User savedUser = userRepository.save(user);

        Post post1 = Post.builder()
                .title("temp")
                .content("content")
                .writer(savedUser)
                .build();

        postRepository.save(post1);

        //when

        List<Post> findPosts = postRepository.findAllPostsAfter(LocalDateTime.MIN);

        //then
        assertThat(findPosts.get(0).getCreatedAt()).isAfter(LocalDateTime.MIN);
        assertThat(findPosts.size()).isEqualTo(1);
    }
}