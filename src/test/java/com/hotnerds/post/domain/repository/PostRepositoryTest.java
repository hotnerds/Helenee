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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

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

        userRepository.save(user);
        post.like(user);
    }

    @DisplayName("게시글 등록 성공")
    @Test
    public void 게시글_등록_성공() {
        //when
        Post savedPost = postRepository.save(post);

        //then
        assertThat(savedPost.getId()).isNotNull();
    }

    @DisplayName("게시글 전체 조회")
    @Test
    public void 게시글_전체_조회_성공() {
        //given
        postRepository.save(post);
        PageRequest page = PageRequest.of(0, 10);

        //when
        List<Post> findPosts = postRepository.findAllPosts(page);

        //then
        assertThat(findPosts).hasSize(1);
    }

    @DisplayName("게시글 제목으로 조회")
    @Test
    public void 게시글_제목으로_조회_성공() {
        //given
        postRepository.save(post);
        PageRequest page = PageRequest.of(0, 10);

        //when
        List<Post> findPosts = postRepository.findAllByTitle(post.getTitle(), page);

        //then
        assertThat(findPosts.size()).isEqualTo(1);
    }

    @DisplayName("특정 시간 이후에 생성된 게시글 조회")
    @Test
    public void 특정시간_이후에_생성된_게시글_조회() {
        //given
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
        postRepository.save(post);
        //when
        List<Post> findPosts = postRepository.findAllByWriter(user, PageRequest.of(0, 10));

        //then
        assertThat(findPosts.size()).isEqualTo(1);
        assertThat(findPosts.get(0).getWriter().getUsername()).isEqualTo(user.getUsername());
    }

    @DisplayName("Post 저장 시 태그를 저장하지 않으면 예외가 발생한다.")
    @Test
    void Post저장시_Tag가_저장안되면_예외_발생() {
        //given
        post.addTag(tag);
        //when, then
        assertThatThrownBy(() -> postRepository.save(post))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);

    }

    @DisplayName("태그 이름으로 게시글을 조회할 수 있다.")
    @Test
    void 태그_이름으로_게시글_조회() {
        //given
        tagRepository.save(tag);
        post.addTag(tag);
        postRepository.save(post);

        //when
        List<Post> findPosts = postRepository.findAllByTagNames(List.of(tag.getName()), PageRequest.of(0, 10));

        //then
        assertThat(findPosts.size()).isEqualTo(1);
    }
}