package com.hotnerds.post.domain.repository;

import com.hotnerds.common.JpaConfig;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JpaConfig.class
))
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("게시글 등록 성공")
    @Test
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

    @DisplayName("게시글 제목으로 조회")
    @Test
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

    @DisplayName("특정 시간 이후에 생성된 게시글 조회")
    @Test
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

    @DisplayName("사용자가 작성한 게시글 조회")
    @Test
    void 사용자가_작성한_게시글_조회() {
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

        Post post2 = Post.builder()
                .title("temp2")
                .content("content2")
                .writer(savedUser)
                .build();


        postRepository.save(post1);
        postRepository.save(post2);

        //when
        List<Post> findPosts = postRepository.findAllByUser(user, PageRequest.of(0, 10));

        //then
        assertThat(findPosts.size()).isEqualTo(2);

        assertThat(findPosts)
                .extracting("title", "content")
                .contains(
                        tuple("temp", "content"),
                        tuple("temp2", "content2")
                );

    }

}