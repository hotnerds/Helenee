package com.hotnerds.unit.comment.repository;

import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.Comments;
import com.hotnerds.comment.repository.CommentRepository;
import com.hotnerds.common.JpaConfig;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.repository.PostRepository;
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
    private Comment comment;
    private User user;

    final static String TEXT = "An apple a day keeps the doctor away";

    @BeforeEach
    void init() {
        Comments comments = new Comments(new ArrayList<>());

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

    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
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

    @DisplayName("????????? ID??? ?????? ??????")
    @Test
    void ?????????_ID???_??????_??????() {
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

        System.out.println(commentList.get(0).getCreatedAt());
        System.out.println(commentList.get(1).getCreatedAt());

        //then
        assertThat(commentList).hasSize(2);

    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
        // given
        userRepository.save(user);
        postRepository.save(post);
        Comment commentReturned = commentRepository.save(comment);

        Long expectedEntryNum = 0L;

        // when
        commentRepository.deleteById(commentReturned.getId());
        Long resultEntryNum = commentRepository.count();

        // then
        assertThat(resultEntryNum).isEqualTo(expectedEntryNum);
    }
}