package com.hotnerds.post.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.*;
import com.hotnerds.post.domain.like.Like;
import com.hotnerds.post.domain.like.Likes;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    User user;

    Post post;

    Likes likes;

    @BeforeEach
    void init() {
        likes = new Likes(new ArrayList<>());

        user = User.builder()
                .username("name")
                .email("email")
                .build();

        post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(user)
                .likes(likes)
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

        when(userRepository.findByUsername(anyString())).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        //when then
        assertThatThrownBy(() -> postService.write(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
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

        assertThatThrownBy(() -> postService.searchByWriter(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
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
        assertThatThrownBy(() -> postService.deletePost(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());

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
        assertThatThrownBy(() -> postService.deletePost(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());

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

    @DisplayName("존재하지 않는 게시글에 좋아요를 누르면 실패 한다.")
    @Test
    void 존재하지않은_게시글_좋아요_실패() {
        //given
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(
                () -> postService.like(user.getUsername(), post.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("존재하지 않는 사용자가 게시글에 좋아요를 요청하면 실패한다.")
    @Test
    void 존재하지않는_사용자_좋아요_실패() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        //when then
        assertThatThrownBy(
                () -> postService.like(user.getUsername(), post.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("게시글 좋아요 요청이 중복되면 예외를 발생시킨다.")
    @Test
    void 게시글_좋아요_중복() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        post.getLikes().add(Like.builder()
                .id(null)
                .user(user)
                .post(post)
                .build());

        //when then
        assertThatThrownBy(
                () -> postService.like(user.getUsername(), post.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATED_LIKE_EXCEPTION.getMessage());
    }

    @DisplayName("사용자가 게시물에 좋아요를 누를 수 있다.")
    @Test
    void 게시글_좋아요_성공() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //when
        LikeResponseDto responseDto = postService.like(user.getUsername(), post.getId());

        //then
        assertAll(
                () -> assertThat(responseDto.getLikeCount()).isEqualTo(1),
                () -> assertThat(responseDto.getUsername()).isEqualTo(user.getUsername()),
                () -> assertThat(responseDto.getPostId()).isEqualTo(post.getId())
        );
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @DisplayName("좋아요를 누르지 않은 게시물에 대해 좋아요를 취소할 수 없다.")
    @Test
    void 좋아요_취소_실패() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //when then
        assertThatThrownBy(
                () -> postService.unlike(user.getUsername(), post.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.LIKE_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("좋아요 취소 성공")
    @Test
    void 존재하지않은_게시글_좋아요_취소() {
        //given
        post.like(user);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));


        //when
        LikeResponseDto responseDto = postService.unlike(user.getUsername(), post.getId());

        //then
        assertAll(
                () -> assertThat(responseDto.getLikeCount()).isEqualTo(0),
                () -> assertThat(responseDto.getUsername()).isEqualTo(user.getUsername()),
                () -> assertThat(responseDto.getPostId()).isEqualTo(post.getId())
        );

        verify(postRepository, times(1)).findById(post.getId());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

}
