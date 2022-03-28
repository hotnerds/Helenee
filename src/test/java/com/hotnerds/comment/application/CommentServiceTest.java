package com.hotnerds.comment.application;

import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Dto.*;
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
import java.util.Arrays;
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
        verify(userRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).findById(anyLong());
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
                .postId(post.getId())
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
        verify(commentRepository, times(1)).save(any());
    }

    @DisplayName("존재하지 않는 댓글에 대한 삭제 요청 시 에러를 발생")
    @Test
    void 존재하지_않는_댓글에_대한_삭제() {
        // given
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .build();
        when(user.getId()).thenReturn(1L);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(reqDto, user.getId()))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @DisplayName("댓글 삭제 요청자와 댓글 작성자의 id가 다르면 에러를 발생")
    @Test
    void 댓글_삭제_타당성_오류_확인() {
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

    @DisplayName("댓글 삭제 성공")
    @Test
    void 댓글_삭제_성공() {
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
        assertThat(post.getComments().getComments().size()).isEqualTo(0);
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).deleteById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @DisplayName("존재하지 않는 댓글에 대한 수정 요청 시 에러 발생")
    @Test
    void 댓글_수정_실패_댓글없음() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .content(NEW_TEXT)
                .build();
        when(user.getId()).thenReturn(1L);

        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto, user.getId()))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION.getMessage());
        verify(commentRepository, times(1)).findById(anyLong());
    }

    @DisplayName("존재하지 않는 유저의 댓글에 대한 수정 요청 시 에러 발생")
    @Test
    void 댓글_수정_실패_유저없음() {
        // given
        String NEW_TEXT = TEXT + "asdf";
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(post.getId()) // id for post
                .commentId(comment.getId()) // id comment
                .content(NEW_TEXT)
                .build();
        when(user.getId()).thenReturn(1L);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // when then
        assertThatThrownBy(() -> commentService.updateComment(reqDto, user.getId()))
                .isInstanceOf(BusinessException.class).hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @DisplayName("댓글 수정 요청자의 타당성이 만족하지 않을 때 에러 발생")
    @Test
    void 댓글_수정_실패_타당성() {
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

    @DisplayName("댓글 수정 성공")
    @Test
    void 댓글_수정_성공() {
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

    @DisplayName("존재하지 않는 게시글에 대한 댓글 요청할 시 에러 발생")
    @Test
    void 게시글_댓글_조회_실패() {
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

    @DisplayName("특정 게시글의 모든 댓글 데이터를 페이징하여서 조회")
    @Test
    void 게시글_댓글_전체_조회() {
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

        List<CommentResponseDto> expectedList = List.of(CommentResponseDto.Of(comment), CommentResponseDto.Of(comment2));

        // when
        List<CommentResponseDto> responseDtoList = commentService.getComments(reqDto);

        // then
        assertAll(
                () -> assertThat(responseDtoList.size()).isEqualTo(2),
                () -> assertThat(responseDtoList)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedList)
        );

        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByPost(any(), any());
    }

}