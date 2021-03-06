package com.hotnerds.unit.comment.application;

import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.dto.*;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.Comments;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
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

    final static String TEXT = "An apple a day keeps the doctor away";

    User user;

    Post post;

    Comment comment;

    Comments comments;

    @BeforeEach
    void init() {
        comments = new Comments(new ArrayList<>());

        user = Mockito.spy(User.builder()
                .username("name")
                .email("email")
                .build());

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

    @DisplayName("?????? ?????? ????????? ??? ?????? ?????? ???????????? ????????????")
    @Test
    void ??????_??????_??????_??????_??????() {
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

    @DisplayName("?????? ?????? ????????? ??? ????????? ????????? ???????????? ?????? ?????? ?????? ??????")
    @Test
    void ??????_??????_??????_??????_??????() {
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

    @DisplayName("?????? ?????? ????????? ??? ???????????? ???????????? ?????? ?????? ?????? ??????")
    @Test
    void ??????_??????_??????_?????????_??????() {
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
        verify(userRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("?????? ?????? ????????? content ????????? 1000 ????????? ??? ?????? ??????")
    @Test
    void ??????_??????_??????_??????() {
        // given
        String LONG_STRING = "";
        for (int i = 0; i < 29; i++) {
            LONG_STRING += TEXT; // LONG_STRING??? ????????? 1008
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

    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L) // id for user
                .postId(post.getId())
                .content(TEXT)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when
        commentService.addComment(reqDto);

        // then
        assertThat(post.getComments().getComments()).hasSize(1);
        verify(userRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @DisplayName("???????????? ?????? ????????? ?????? ?????? ?????? ??? ????????? ??????")
    @Test
    void ????????????_??????_?????????_??????_??????() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .build();
        Long userId = 2L;
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(reqDto, userId))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
    }

    @DisplayName("?????? ?????? ???????????? ?????? ???????????? id??? ????????? ????????? ??????")
    @Test
    void ??????_??????_?????????_??????_??????() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .build();
        when(user.getId()).thenReturn(1L);
        comments.add(comment);
        post = Post.builder()
                .id(post.getId())
                .title("title")
                .content("content")
                .writer(user)
                .comments(comments)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(reqDto, 2L))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_INVALID_EXCEPTION.getMessage());
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void ??????_??????_??????() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .build();
        when(user.getId()).thenReturn(1L);
        comments.add(comment);
        post = Post.builder()
                .id(post.getId())
                .title("title")
                .content("content")
                .writer(user)
                .comments(comments)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when
        commentService.deleteComment(reqDto, user.getId());

        // then
        assertThat(post.getComments().getComments()).hasSize(0);
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).deleteById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
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
        Long userId = 1L;

        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto, userId))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
        verify(commentRepository, times(1)).findById(anyLong());
    }

    @DisplayName("???????????? ?????? ????????? ????????? ?????? ?????? ?????? ??? ?????? ??????")
    @Test
    void ??????_??????_??????_????????????() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .content(NEW_TEXT)
                .build();
        Long userId = 2L;
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto, userId))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
        verify(commentRepository, times(1)).findById(any());
        verify(userRepository, times(1)).findById(any());
    }

    @DisplayName("?????? ?????? ???????????? ???????????? ???????????? ?????? ??? ?????? ??????")
    @Test
    void ??????_??????_??????_?????????() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .content(NEW_TEXT)
                .build();
        when(user.getId()).thenReturn(1L);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto, 2L))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_INVALID_EXCEPTION.getMessage());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
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
        when(user.getId()).thenReturn(1L);
        comments.add(comment);
        post = Post.builder()
                .id(post.getId())
                .title("title")
                .content("content")
                .writer(user)
                .comments(comments)
                .build();

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when
        commentService.updateComment(reqDto, user.getId());

        // then
        assertThat(post.getComments().getComments().get(0).getContent()).isEqualTo(NEW_TEXT);
        verify(commentRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
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

        Comment comment2 = Comment.builder()
                .id(2L)
                .writer(user)
                .post(post)
                .content(TEXT)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPost(any(Post.class), any(PageRequest.class))).thenReturn(List.of(comment, comment2));

        List<CommentResponseDto> expectedList = List.of(CommentResponseDto.of(comment), CommentResponseDto.of(comment2));

        // when
        List<CommentResponseDto> responseDtoList = commentService.getComments(reqDto);

        // then
        assertAll(
                () -> assertThat(responseDtoList).hasSize(2),
                () -> assertThat(responseDtoList)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedList)
        );

        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByPost(any(), any());
    }

}