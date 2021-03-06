package com.hotnerds.integration.comment;

import com.hotnerds.integration.IntegrationTest;
import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.Comments;
import com.hotnerds.comment.domain.dto.*;
import com.hotnerds.comment.repository.CommentRepository;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

class CommentServiceIntegrationTest extends IntegrationTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentService commentService;

    final static String TEXT = "An apple a day keeps the doctor away";
    final static Long userId = 1L;

    User user;

    Post post;

    Comment comment;

    Comments comments;

    @BeforeEach
    void init() {
        databaseCleaner.clean();

        comments = new Comments(new ArrayList<>());

        user = User.builder()
                .username("name")
                .email("email")
                .build();

        post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(user)
                .comments(comments)
                .build();

        comment = Comment.builder()
                .id(1L)
                .writer(user)
                .post(post)
                .content(TEXT)
                .build();
    }

    @DisplayName("?????? ?????? ????????? ??? ?????? ?????? ???????????? ?????? ??????")
    @Test
    void ??????_??????_??????_??????_??????() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content("")
                .build();

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_INVALID_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ????????? ??? ????????? ????????? ???????????? ?????? ?????? ?????? ??????")
    @Test
    void ??????_??????_??????_??????_??????() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ????????? ??? ???????????? ???????????? ?????? ?????? ?????? ??????")
    @Test
    void ??????_??????_??????_?????????_??????() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        userRepository.save(user);

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ????????? content ????????? 1000 ????????? ??? ?????? ??????")
    @Test
    void ??????_??????_??????_??????() {
        // given
        String LONG_STRING = "";
        for (int i = 0; i < 29; i++) {
            LONG_STRING += TEXT;
        }
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(LONG_STRING)
                .build();

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_INVALID_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        userRepository.save(user);
        postRepository.save(post);

        // when
        commentService.addComment(reqDto);

        // then
        assertThat(postRepository.getById(post.getId()).getAllComments()).hasSize(1);
    }

    @DisplayName("???????????? ?????? ????????? ?????? ?????? ?????? ??? ????????? ??????")
    @Test
    void ????????????_??????_?????????_??????_??????() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(post.getId())
                .commentId(comment.getId())
                .build();
        userRepository.save(user);
        postRepository.save(post);

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(reqDto, userId))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ???????????? ?????? ???????????? id??? ????????? ????????? ??????")
    @Test
    void ??????_??????_?????????_??????_??????() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(post.getId())
                .commentId(comment.getId())
                .build();
        userRepository.save(user);
        postRepository.save(post);
        CommentCreateReqDto createReqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        commentService.addComment(createReqDto);

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(reqDto, 2L))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_INVALID_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(post.getId())
                .commentId(comment.getId())
                .build();
        userRepository.save(user);
        postRepository.save(post);
        CommentCreateReqDto createReqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        commentService.addComment(createReqDto);

        // when
        commentService.deleteComment(reqDto, user.getId());

        // then
        assertThat(postRepository.getById(post.getId()).getAllComments()).isEmpty();
    }

    @DisplayName("???????????? ?????? ????????? ?????? ?????? ?????? ??? ?????? ??????")
    @Test
    void ??????_??????_??????_????????????() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .content(NEW_TEXT)
                .build();
        userRepository.save(user);
        postRepository.save(post);

        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto, userId))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ???????????? ???????????? ???????????? ?????? ??? ?????? ??????")
    @Test
    void ??????_??????_??????_?????????() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        Long wrongUserId = 2L;
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .content(NEW_TEXT)
                .build();
        userRepository.save(user);
        postRepository.save(post);
        CommentCreateReqDto createReqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        commentService.addComment(createReqDto);

        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto, wrongUserId))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_INVALID_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .content(NEW_TEXT)
                .build();
        userRepository.save(user);
        postRepository.save(post);
        CommentCreateReqDto createReqDto = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        commentService.addComment(createReqDto);

        // when
        commentService.updateComment(reqDto, userId);

        // then
        assertThat(postRepository.findById(reqDto.getPostId()).get().getAllComments().get(0).getContent()).isEqualTo(NEW_TEXT);
    }

    @DisplayName("???????????? ?????? ???????????? ?????? ?????? ????????? ??? ?????? ??????")
    @Test
    void ?????????_??????_??????_??????() {
        // given
        Long postId = 2L;
        CommentByPostReqDto reqDto = CommentByPostReqDto.builder()
                .postId(postId)
                .pageable(PageRequest.of(0, 10))
                .build();

        // when then
        assertThatThrownBy(() -> commentService.getComments(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("?????? ???????????? ?????? ?????? ???????????? ?????????????????? ??????")
    @Test
    void ?????????_??????_??????_??????() {
        // given
        CommentByPostReqDto reqDto = CommentByPostReqDto.builder()
                .postId(post.getId())
                .pageable(PageRequest.of(0, 10))
                .build();
        userRepository.save(user);
        postRepository.save(post);
        CommentCreateReqDto createReqDto1 = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        CommentCreateReqDto createReqDto2 = CommentCreateReqDto.builder()
                .userId(userId)
                .postId(post.getId())
                .content(TEXT)
                .build();
        commentService.addComment(createReqDto1);
        commentService.addComment(createReqDto2);
        Comment comment2 = Comment.builder()
                .id(2L)
                .writer(user)
                .post(post)
                .content(TEXT)
                .build();
        List<CommentResponseDto> expectedList = List.of(CommentResponseDto.of(comment), CommentResponseDto.of(comment2));

        // when
        List<CommentResponseDto> responseDtoList = commentService.getComments(reqDto);

        // then
        assertThat(responseDtoList).hasSize(2);
    }

}
