package com.hotnerds.post.application;

import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.comment.Comment;
import com.hotnerds.post.domain.comment.Comments;
import com.hotnerds.post.domain.dto.*;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.post.exception.CommentInvalidException;
import com.hotnerds.post.exception.PostNotFoundException;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.hotnerds.user.exception.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    PostService postService;

    String TEXT = "An apple a day keeps the doctor away";

    User user;

    Post post;

    Comment comment;

    Comments comments;

    @BeforeEach
    void init() {
        comments = new Comments(new ArrayList<>());

        comment = Comment.builder()
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

    @DisplayName("게시글 등록")
    @Test
    void 게시글_등록() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .username("username")
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        //when
        postService.write(requestDto);

        //then
        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @DisplayName("유효하지 않은 사용자는 게시물을 등록할 수 없다.")
    @Test
    void 유효하지않은사용자_게시글_생성_실패() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .username("username")
                .build();

        when(userRepository.findByUsername(anyString())).thenThrow(UserNotFoundException.class);

        //when then
        assertThrows(UserNotFoundException.class, () -> postService.write(requestDto));
        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
    }

    @DisplayName("게시글 제목으로 조회")
    @Test
    void 게시글_제목으로_조회() {
        //given
        when(postRepository.findAllByTitle(anyString())).thenReturn(List.of(post));
        //when
        List<PostResponseDto> findPosts = postService.searchByTitle(post.getTitle());

        //then
        assertThat(findPosts.size()).isEqualTo(1);

        verify(postRepository, times(1)).findAllByTitle(post.getTitle());
    }

    @DisplayName("유효하지 않은 사용자 게시물 조회 실패")
    @Test
    void 유효하지않은사용자_게시물_조회_실패() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        PostByUserRequestDto requestDto = PostByUserRequestDto.builder()
                .username(user.getUsername())
                .pageable(PageRequest.of(0, 10))
                .build();

        //when then
        assertThrows(UserNotFoundException.class, () -> postService.searchByWriter(requestDto));

        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @DisplayName("사용자가 작성한 게시물 조회")
    @Test
    void 사용자가_작성한_게시물_조회() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findAllByUser(any(User.class), any(PageRequest.class))).thenReturn(List.of(post, post));

        PostByUserRequestDto requestDto = PostByUserRequestDto.builder()
                .username(user.getUsername())
                .pageable(PageRequest.of(0, 10))
                .build();

        List<PostResponseDto> expectedResult = List.of(
                PostResponseDto.of(post),
                PostResponseDto.of(post)
        );

        //when
        List<PostResponseDto> findResults = postService.searchByWriter(requestDto);

        //then
        assertThat(findResults.size()).isEqualTo(2);

        assertThat(findResults)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findAllByUser(user, requestDto.getPageable());


    }


    @DisplayName("유효하지 않은 사용자는 게시글을 삭제할 수 없다.")
    @Test
    void 유효하지않은사용자_게시글_삭제_실패() {
        //given
        PostDeleteRequestDto requestDto = PostDeleteRequestDto.builder()
                .postId(1L)
                .username("garam")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when then
        assertThrows(UserNotFoundException.class, () -> postService.deletePost(requestDto));

        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());

    }

    @DisplayName("존재하지 않은 게시글 삭제 실패")
    @Test
    void 존재하지않은_게시글_삭제_실패() {
        //given
        PostDeleteRequestDto requestDto = PostDeleteRequestDto.builder()
                .postId(1L)
                .username("garam")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThrows(PostNotFoundException.class, () -> postService.deletePost(requestDto));

        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(postRepository, times(1)).findById(requestDto.getPostId());
    }

    @DisplayName("게시글 삭제 성공")
    @Test
    void 게시글_삭제_성공() {
        //given
        PostDeleteRequestDto requestDto = PostDeleteRequestDto.builder()
                .postId(1L)
                .username("garam")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //when
        postService.deletePost(requestDto);

        //then
        verify(userRepository, times(1)).findByUsername(requestDto.getUsername());
        verify(postRepository, times(1)).findById(requestDto.getPostId());
        verify(postRepository, times(1)).deleteById(requestDto.getPostId());
    }

    @DisplayName("댓글 추가 요청할 때 본문 값이 공백이면 예외발생")
    @Test
    void 댓글_추가_실패_내용_공백() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(user.getId())
                .postId(post.getId())
                .content("")
                .build();

        // when then
        assertThrows(CommentInvalidException.class, () -> postService.addComment(reqDto));

    }

    @DisplayName("댓글 추가 요청할 때 요청한 유저가 존재하지 않을 경우 예외 발생")
    @Test
    void 댓글_추가_실패_유저_없음() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(user.getId())
                .postId(post.getId())
                .content("")
                .build();
        when(userRepository.findById(anyLong())).thenThrow(UserNotFoundException.class);
        when(postRepository.findById(anyLong())).thenThrow(PostNotFoundException.class);

        // when then
        assertThrows(UserNotFoundException.class, () -> postService.addComment(reqDto));

    }

    @DisplayName("댓글 추가 요청할 때 게시글이 존재하지 않을 경우 예외 발생")
    @Test
    void 댓글_추가_실패_게시글_없음() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(user.getId())
                .postId(post.getId())
                .content("")
                .build();

        // when then
        assertThrows(PostNotFoundException.class, () -> postService.addComment(reqDto));
    }

    @DisplayName("댓글 추가 성공")
    @Test
    void 댓글_추가_성공() {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L) // id for user
                .postId(post.getId())
                .content(TEXT)
                .build();

        comments.add(comment);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when
        postService.addComment(reqDto);

        // then
        assertTrue(post.getComments().contains(comment));
        verify(userRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("댓글 생성 요청의 content 길이가 1000 이상일 때 에러 발생")
    @Test
    void 댓글_내용_제한() {
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
        assertThrows(CommentInvalidException.class, () -> postService.addComment(reqDto));
    }

    @DisplayName("댓글 삭제 요청할 때 게시글 혹은 유저가 존재하지 않으면 예외 발생")
    @Test
    void 유효하지않은_게시글_유저_댓글_삭제() {
        // given
        Post wrongPost = Post.builder()
                .id(2L)
                .title("wrong")
                .content("wrong")
                .writer(user)
                .build();

        User wrongUser = User.builder()
                .username("wrong")
                .email("wrong")
                .build();

        CommentDeleteReqDto reqDtoWrongPost = CommentDeleteReqDto.builder()
                .userId(user.getId())
                .postId(wrongPost.getId())
                .commentId(1L)
                .build();

        CommentDeleteReqDto reqDtoWrongUser = CommentDeleteReqDto.builder()
                .userId(wrongUser.getId())
                .postId(post.getId())
                .commentId(2L)
                .build();

        when(postRepository.getById(1L)).thenReturn(post);
        when(postRepository.getById(2L)).thenReturn(wrongPost);
        when(userRepository.getById(anyLong())).thenReturn(user);

        // when then
        assertAll(
                () -> assertThrows(PostNotFoundException.class, () -> postService.deleteComment(reqDtoWrongPost)),
                () -> assertThrows(UserNotFoundException.class, () -> postService.deleteComment(reqDtoWrongUser))
        );
        verify(postRepository, times(2)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
    }
}
