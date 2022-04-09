package com.hotnerds.post.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.*;
import com.hotnerds.post.domain.like.Like;
import com.hotnerds.post.domain.like.Likes;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.tag.application.TagService;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Mock
    TagService tagService;

    @InjectMocks
    PostService postService;

    User user;

    AuthenticatedUser authUser;

    Post post;

    Likes likes;

    Tag tag;

    @BeforeEach
    void init() {
        likes = new Likes(new ArrayList<>());

        user = User.builder()
                .username("name")
                .email("email")
                .build();

        authUser = AuthenticatedUser.of(user);

        post = new Post(1L,"title", "content", user);

        tag = new Tag("tagName");
    }

    @DisplayName("게시글 등록")
    @Test
    void 게시글_등록_성공() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tagService.findOrCreateTag(anyString())).thenReturn(tag);

        //when
        Long postId = postService.write(requestDto, authUser);

        //then
        assertThat(postId).isNotNull();
        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).save(any());
        verify(tagService, times(requestDto.getTagNames().size())).findOrCreateTag(any());
    }

    @DisplayName("유효하지 않은 사용자는 게시물을 등록할 수 없다.")
    @Test
    void 유효하지않은사용자_게시글_생성_실패() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(userRepository.findByUsername(anyString())).thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        //when then
        assertThatThrownBy(() -> postService.write(requestDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
        verify(userRepository, times(1)).findByUsername(any());
    }


    @DisplayName("유효하지 않은 태그 이름을 갖는 게시물은 생성할 수 없다.")
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", " "})
    @ParameterizedTest
    public void 유효하지않는_태그를_갖는_게시글_생성_실패(String tagName) {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tagName))
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tagService.findOrCreateTag(anyString())).thenThrow(new BusinessException(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION));

        //when then
        assertThatThrownBy(() -> postService.write(requestDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);

        verify(userRepository, times(1)).findByUsername(any());
        verify(tagService, times(1)).findOrCreateTag(any());
    }

    @DisplayName("중복된 태그를 가진 게시물은 생성할 수 없다.")
    @Test
    public void 중복된_태그를_가진_게시물_생성_실패() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(Arrays.asList(tag.getName(), tag.getName()))
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tagService.findOrCreateTag(anyString())).thenReturn(tag);

        //when then
        assertThatThrownBy(() -> postService.write(requestDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.DUPLICATED_TAG_EXCEPTION);

        verify(userRepository, times(1)).findByUsername(any());
        verify(tagService, times(requestDto.getTagNames().size())).findOrCreateTag(any());
    }

    @DisplayName("전체 게시글 조회")
    @Test
    void 전체_게시글_조회() {
        //given
        when(postRepository.findAllPosts(any())).thenReturn(List.of(post));
        Pageable page = PageRequest.of(0, 10);
        //when
        List<PostResponseDto> posts = postService.searchAll(page);

        //then
        assertThat(posts.size()).isEqualTo(1);
        verify(postRepository, times(1)).findAllPosts(any());
    }

    @DisplayName("게시글 제목으로 조회")
    @Test
    void 게시글_제목으로_조회() {
        //given
        when(postRepository.findAllByTitle(any(), any())).thenReturn(List.of(post));
        PostByTitleRequestDto requestDto = PostByTitleRequestDto.builder()
                .title(post.getTitle())
                .pageable(PageRequest.of(0, 10))
                .build();
        //when
        List<PostResponseDto> findPosts = postService.searchByTitle(requestDto);

        //then
        assertThat(findPosts.size()).isEqualTo(1);

        verify(postRepository, times(1)).findAllByTitle(any(), any());
    }

    @DisplayName("존재하지 않은 사용자 게시물 조회 실패")
    @Test
    void 존재하지_않은_사용자_게시물_조회_실패() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        PostByWriterRequestDto requestDto = PostByWriterRequestDto.builder()
                .writer(user.getUsername())
                .pageable(PageRequest.of(0, 10))
                .build();

        //when then

        assertThatThrownBy(() -> postService.searchByWriter(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @DisplayName("게시글 작성자 이름으로 게시글 조회.")
    @Test
    void 작성자_이름으로_게시물_조회() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findAllByWriter(any(User.class), any(PageRequest.class))).thenReturn(List.of(post, post));

        PostByWriterRequestDto requestDto = PostByWriterRequestDto.builder()
                .writer(user.getUsername())
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
        verify(postRepository, times(1)).findAllByWriter(user, requestDto.getPageable());
    }

    @DisplayName("특정 태그가 붙어 있는 게시글 조회할 수 있다.")
    @Test
    void 태그로_게시글_조회() {
        //given
        post.addTag(tag);
        PostByTagRequestDto requestDto = PostByTagRequestDto.builder()
                .tagNames(List.of(tag.getName()))
                .pageable(PageRequest.of(0, 10))
                .build();

        when(postRepository.findAllByTagNames(any(), any())).thenReturn(List.of(post));

        //when
        List<PostResponseDto> findPosts = postService.searchByTagNames(requestDto);

        //then
        assertThat(findPosts.size()).isEqualTo(1);

        verify(postRepository, times(1)).findAllByTagNames(any(),any());
    }

    @DisplayName("유효하지 않은 태그 이름으로 게시글 조회 시 예외가 발생한다.")
    @Test
    void 유효하지_않은_태그이름_게시글_조회_실패() {
        //given
        post.addTag(tag);
        PostByTagRequestDto requestDto = PostByTagRequestDto.builder()
                .tagNames(List.of("", "     "))
                .pageable(PageRequest.of(0, 10))
                .build();

        //when then
        assertThatThrownBy(() -> postService.searchByTagNames(requestDto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);
    }

    @DisplayName("유효하지 않은 사용자는 게시글을 삭제할 수 없다.")
    @Test
    void 유효하지않은사용자_게시글_삭제_실패() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.delete(1L, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());

    }

    @DisplayName("존재하지 않은 게시글 삭제 실패")
    @Test
    void 존재하지않은_게시글_삭제_실패() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.delete(1L, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findById(any());
    }

    @DisplayName("게시글 삭제 성공")
    @Test
    void 게시글_삭제_성공() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //when
        postService.delete(1L, authUser);

        //then
        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findById(any());
        verify(postRepository, times(1)).deleteById(any());
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

    @DisplayName("게시글 수정 성공")
    @Test
    void 게시글_수정_성공() {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .username(user.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tagService.findOrCreateTag(anyString())).thenReturn(tag);

        //when
        postService.update(updateDto);

        //then
        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tagService, times(updateDto.getTagNames().size())).findOrCreateTag(anyString());
    }

    @DisplayName("게시글 작성자가 아닌 사용자는 게시글 수정할 수 없다.")
    @Test
    public void 작성자가_아닌_사용자_게시글_수정_실패() {
        //given
        User notWriter = User.builder()
                .username("garam1234")
                .email("kgr@naver.com")
                .build();
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .username(notWriter.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(notWriter));

        //when then
        assertThatThrownBy(() -> postService.update(updateDto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_WRITER_NOT_MATCH_EXCEPTION);

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @DisplayName("수정하려는 게시글이 없으면 예외가 발생한다.")
    @Test
    void 수정하려는_게시글이_없을때_예외_발생() {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .username(user.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.update(updateDto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_NOT_FOUND_EXCEPTION);

        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("수정하려는 게시글의 사용자가 존재하지 않으면 예외가 발생한다.")
    @Test
    void 수정하려는_게시글_사용자가_없을때_예외_발생() {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .username(user.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.update(updateDto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @DisplayName("수정하려는 게시글에 태그 이름이 유효하지 않으면 예외가 발생한다.")
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", " "})
    @ParameterizedTest
    void 수정하려는_게시글_유효하지_않은_태그를_갖으면_예외_발생(String tagName) {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .username(user.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tagName))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tagService.findOrCreateTag(anyString())).thenThrow(new BusinessException(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION));

        //when then
        assertThatThrownBy(() -> postService.update(updateDto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tagService, times(1)).findOrCreateTag(anyString());
    }
}
