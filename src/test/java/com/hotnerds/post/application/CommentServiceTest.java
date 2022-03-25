package com.hotnerds.post.application;

import com.hotnerds.comment.application.CommentService;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.comment.Comment;
import com.hotnerds.post.domain.comment.Comments;
import com.hotnerds.post.domain.dto.CommentCreateReqDto;
import com.hotnerds.post.domain.dto.CommentDeleteReqDto;
import com.hotnerds.post.domain.dto.CommentResponseDto;
import com.hotnerds.post.domain.dto.CommentUpdateReqDto;
import com.hotnerds.comment.repository.CommentRepository;
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

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    String TEXT = "An apple a day keeps the doctor away";

    User user;

    Post post;

    Comment comment;

    Comments comments;

    @BeforeEach
    void init() {
        comments = new Comments(new ArrayList<>());

        comment = Comment.builder()
                .id(1L)
                .writer(user)
                .post(post)
                .content(TEXT)
                .build();

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
    }

    @DisplayName("댓글 생성 요청할 때 본문 값이 공백이면 예외발생")
    @Test
    void 댓글_추가_실패_내용_공백() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L)
                .postId(post.getId())
                .content("")
                .build();

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_INVALID_EXCEPTION.getMessage());
    }

    @DisplayName("댓글 생성 요청할 때 요청한 유저가 존재하지 않을 경우 예외 발생")
    @Test
    void 댓글_추가_실패_유저_없음() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L)
                .postId(post.getId())
                .content(TEXT)
                .build();

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("댓글 생성 요청할 때 게시글이 존재하지 않을 경우 예외 발생")
    @Test
    void 댓글_추가_실패_게시글_없음() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L)
                .postId(post.getId())
                .content(TEXT)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("댓글 생성 요청의 content 길이가 1000 이상일 때 에러 발생")
    @Test
    void 댓글_내용_길이_제한() {
        // given
        String LONG_STRING = "";
        for (int i = 0; i < 29; i++) {
            LONG_STRING += TEXT; // LONG_STRING의 길이는 1008
        }
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L) // id for user
                .postId(post.getId())
                .content(LONG_STRING)
                .build();

        // when then
        assertThatThrownBy(() -> commentService.addComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_INVALID_EXCEPTION.getMessage());
    }

    @DisplayName("댓글 생성 성공")
    @Test
    void 댓글_추가_성공() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L) // id for user
                .postId(1L)
                .content(TEXT)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when
        commentService.addComment(reqDto);

        // then
        assertThat(post.getComments().getComments().size()).isEqualTo(1);
        verify(userRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("존재하지 않는 댓글에 대한 삭제 요청 시 에러를 발생")
    @Test
    void 존재하지_않는_댓글에_대한_삭제() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(1L) // id for post
                .commentId(1L) // id comment
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("댓글 삭제 성공")
    @Test
    void 댓글_삭제_성공() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(1L) // id for post
                .commentId(1L) // id comment
                .build();

        comments.add(comment);

        post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(user)
                .comments(comments)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when
        commentService.deleteComment(reqDto);

        // then
        assertThat(post.getComments().getComments().size()).isEqualTo(0);
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("존재하지 않는 댓글에 대한 수정 요청 시 에러 발생")
    @Test
    void 댓글_수정_실패() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(1L) // id for post
                .commentId(1L) // id comment
                .content(NEW_TEXT)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("댓글 수정 성공")
    @Test
    void 댓글_수정_성공() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(1L) // id for post
                .commentId(1L) // id comment
                .content(NEW_TEXT)
                .build();

        comments.add(comment);
        post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(user)
                .comments(comments)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // when
        commentService.updateComment(reqDto);

        // then
        assertThat(post.getComments().getComments().get(0).getContent()).isEqualTo(NEW_TEXT);
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("존재하지 않는 게시글에 대한 댓글 요청할 시 에러 발생")
    @Test
    void 게시글_댓글_조회_실패() {
        // given
        Long postId = 2L;

        // when then
        assertThatThrownBy(() -> commentService.getComments(postId))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("특정 게시글의 모든 댓글 데이터를 조회")
    @Test
    void 게시글_댓글_전체_조회() {
        // given
        Comment comment2 = Comment.builder()
                .id(2L)
                .writer(user)
                .post(post)
                .content(TEXT)
                .build();

        comments.add(comment);
        comments.add(comment2);
        post = Post.builder()
                .id(1L)
                .title("title")
                .content(TEXT)
                .writer(user)
                .comments(comments)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when
        CommentResponseDto commentList = commentService.getComments(1L);

        // then
        assertAll(
                () -> assertThat(post.getComments().getComments().size()).isEqualTo(2),
                () -> assertEquals(comment, commentList.getCommentList().get(0)),
                () -> assertEquals(comment2, commentList.getCommentList().get(1))
        );
    }

}