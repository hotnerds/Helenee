package com.hotnerds.post.domain.repository;

import com.hotnerds.common.JpaConfig;
import com.hotnerds.post.domain.Post;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.tag.domain.repository.TagRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JpaConfig.class
))
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    private User user;
    private Post post;
    private Tag tag;

    @BeforeEach
    void init() {
        user = User.builder()
                .username("username")
                .email("email")
                .build();

        post = new Post("title", "content", user);

        tag = new Tag("tagName");

        post.like(user);
        post.addTag(tag);
    }

    @DisplayName("게시글 등록 성공")
    @Test
    public void 게시글_등록_성공() {
        //given
        userRepository.save(user);
        tagRepository.save(tag);
        //when
        Post savedPost = postRepository.save(post);


        //then
        assertAll(
                () -> assertThat(savedPost.getId()).isNotNull(),
                () -> assertThat(savedPost.getTitle()).isEqualTo(post.getTitle()),
                () -> assertThat(savedPost.getContent()).isEqualTo(post.getContent()),
                () -> assertThat(savedPost.getWriter().getId()).isNotNull(),
                () -> assertThat(savedPost.getLikeCount()).isEqualTo(1),
                () -> assertThat(savedPost.getTagNames().size()).isEqualTo(1));

    }

    @DisplayName("게시글 제목으로 조회")
    @Test
    public void 게시글_제목으로_조회_성공() {
        //given
        userRepository.save(user);
        tagRepository.save(tag);
        postRepository.save(post);

        //when
        List<Post> findPosts = postRepository.findAllByTitle(post.getTitle());


        //then
        assertThat(findPosts.size()).isEqualTo(1);
    }

    @DisplayName("특정 시간 이후에 생성된 게시글 조회")
    @Test
    public void 특정시간_이후에_생성된_게시글_조회() {
        //given
        userRepository.save(user);
        tagRepository.save(tag);
        postRepository.save(post);

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
        userRepository.save(user);
        tagRepository.save(tag);
        postRepository.save(post);
        //when
        List<Post> findPosts = postRepository.findAllByUser(user, PageRequest.of(0, 10));

        //then
        assertThat(findPosts.size()).isEqualTo(1);
        assertThat(findPosts.get(0).getWriter().getUsername()).isEqualTo(user.getUsername());
    }

}