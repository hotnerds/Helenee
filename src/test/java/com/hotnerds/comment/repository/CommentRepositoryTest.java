package com.hotnerds.comment.repository;

import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.Comments;
import com.hotnerds.common.JpaConfig;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.tag.domain.Tag;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JpaConfig.class
))
class CommentRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Post post;
    private Comments comments;
    private Comment comment;
    private User user;

    final static String TEXT = "An apple a day keeps the doctor away";

    @BeforeEach
    void init() {
        comments = new Comments(new ArrayList<>());

        user = User.builder()
                .username("user")
                .email("email")
                .build();

        post = Post.builder()
                .title("title")
                .content("content")
                .writer(user)
                .comments(comments)
                .build();

        comment = Comment.builder()
                .id(1L)
                .post(post)
                .writer(user)
                .content(TEXT)
                .build();
    }

    @DisplayName("댓글 등록 성공")
    @Test
    public void 댓글_등록_성공() {
        //given
        userRepository.save(user);
        postRepository.save(post);

        //when
        Comment savedComment = commentRepository.save(comment);

        //then
        assertAll(
                () -> assertThat(savedComment.getId()).isNotNull(),
                () -> assertThat(savedComment.getPost().getId()).isEqualTo(comment.getPost().getId()),
                () -> assertThat(savedComment.getWriter()).isEqualTo(comment.getWriter()),
                () -> assertThat(savedComment.getContent()).isEqualTo(comment.getContent()));
    }

    @DisplayName("게시글 ID로 댓글 조회")
    @Test
    public void 게시글_ID로_댓글_조회() {
        //given
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        Comment comment2 = Comment.builder()
                .post(post)
                .writer(user)
                .content(TEXT + "text")
                .build();
        commentRepository.save(comment2);

        //when
        List<Comment> commentList = commentRepository.findAllByPost(post, PageRequest.of(0, 10));

        //then
        assertAll(
                () -> assertThat(commentList.size()).isEqualTo(2),
                () -> assertThat(commentList.get(0).getContent()).isEqualTo(comment.getContent()),
                () -> assertThat(commentList.get(1).getContent()).isEqualTo(comment2.getContent())
        );

    }

    @DisplayName("댓글 삭제 성공")
    @Test
    public void 댓글_삭제_성공() {
        // given
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        int expectedEntryNum = 0;

        // when
        commentRepository.deleteById(comment.getId());
        int resultEntryNum = commentRepository.findAll().size();

        // then
        assertThat(resultEntryNum).isEqualTo(expectedEntryNum);
    }
}