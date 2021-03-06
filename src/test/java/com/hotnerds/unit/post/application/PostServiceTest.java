package com.hotnerds.unit.post.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.post.application.PostService;
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
class PostServiceTest {

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

    @DisplayName("????????? ??????")
    @Test
    void ?????????_??????_??????() {
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

    @DisplayName("???????????? ?????? ???????????? ???????????? ????????? ??? ??????.")
    @Test
    void ???????????????????????????_?????????_??????_??????() {
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


    @DisplayName("???????????? ?????? ?????? ????????? ?????? ???????????? ????????? ??? ??????.")
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", " "})
    @ParameterizedTest
    void ??????????????????_?????????_??????_?????????_??????_??????(String tagName) {
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

    @DisplayName("????????? ????????? ?????? ???????????? ????????? ??? ??????.")
    @Test
    void ?????????_?????????_??????_?????????_??????_??????() {
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

    @DisplayName("?????? ????????? ??????")
    @Test
    void ??????_?????????_??????() {
        //given
        when(postRepository.findAllPosts(any())).thenReturn(List.of(post));
        Pageable pageable = PageRequest.of(0, 10);
        //when
        List<PostResponseDto> posts = postService.searchAll(pageable);

        //then
        assertThat(posts).hasSize(1);
        verify(postRepository, times(1)).findAllPosts(any());
    }

    @DisplayName("????????? Id??? ?????? ??????")
    @Test
    void ?????????_Id???_??????_??????() {
        //given
        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        //when
        PostResponseDto postResponseDto = postService.searchByPostId(post.getId());

        //then
        assertThat(postResponseDto.getPostId()).isEqualTo(post.getId());

        verify(postRepository, times(1)).findById(any());
    }

    @DisplayName("???????????? Id??? ?????? ???????????? ????????? ?????? ??????")
    @Test
    void ?????????_Id_?????????_??????_??????() {
        //given
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.searchByPostId(2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_NOT_FOUND_EXCEPTION);

        verify(postRepository, times(1)).findById(any());
    }

    @DisplayName("????????? ???????????? ??????")
    @Test
    void ?????????_????????????_??????() {
        //given
        when(postRepository.findAllByTitle(any(), any())).thenReturn(List.of(post));
        PostByTitleRequestDto requestDto = PostByTitleRequestDto.builder()
                .title(post.getTitle())
                .pageable(PageRequest.of(0 ,10))
                .build();
        //when
        List<PostResponseDto> findPosts = postService.searchByTitle(requestDto);

        //then
        assertThat(findPosts).hasSize(1);

        verify(postRepository, times(1)).findAllByTitle(any(), any());
    }

    @DisplayName("???????????? ?????? ????????? ????????? ?????? ??????")
    @Test
    void ????????????_??????_?????????_?????????_??????_??????() {
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

    @DisplayName("????????? ????????? ???????????? ????????? ??????.")
    @Test
    void ?????????_????????????_?????????_??????() {
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
        assertThat(findResults).hasSize(2);

        assertThat(findResults)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);

        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findAllByWriter(any(), any());
    }

    @DisplayName("?????? ????????? ?????? ?????? ????????? ????????? ??? ??????.")
    @Test
    void ?????????_?????????_??????() {
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
        assertThat(findPosts).hasSize(1);

        verify(postRepository, times(1)).findAllByTagNames(any(),any());
    }

    @DisplayName("???????????? ?????? ?????? ???????????? ????????? ?????? ??? ????????? ????????????.")
    @Test
    void ????????????_??????_????????????_?????????_??????_??????() {
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

    @DisplayName("???????????? ?????? ???????????? ???????????? ????????? ??? ??????.")
    @Test
    void ???????????????????????????_?????????_??????_??????() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.delete(1L, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());

    }

    @DisplayName("???????????? ?????? ????????? ?????? ??????")
    @Test
    void ??????????????????_?????????_??????_??????() {
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

    @DisplayName("????????? ???????????? ?????? ????????? ????????? ????????? ?????? ??? ??? ??????.")
    @Test
    void ?????????_????????????_?????????_?????????_?????????_??????_??????() {
        User otherUser = new User("otherUser", "aaa@aaa");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(otherUser));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //when then
        assertThatThrownBy(() -> postService.delete(1L, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_WRITER_NOT_MATCH_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findById(any());
    }

    @DisplayName("????????? ?????? ??????")
    @Test
    void ?????????_??????_??????() {
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

    @DisplayName("???????????? ?????? ???????????? ???????????? ????????? ?????? ??????.")
    @Test
    void ??????????????????_?????????_?????????_??????() {
        //given
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        Long postId = post.getId();

        //when then
        assertThatThrownBy(
                () -> postService.like(postId, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findById(any());
    }

    @DisplayName("???????????? ?????? ???????????? ???????????? ???????????? ???????????? ????????????.")
    @Test
    void ??????????????????_?????????_?????????_??????() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        Long postId = post.getId();

        //when then
        assertThatThrownBy(
                () -> postService.like(postId, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());
    }

    @DisplayName("????????? ????????? ????????? ???????????? ????????? ???????????????.")
    @Test
    void ?????????_?????????_??????() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        Long postId = post.getId();

        post.getLikeList().add(Like.builder()
                .id(null)
                .user(user)
                .post(post)
                .build());

        //when then
        assertThatThrownBy(
                () -> postService.like(postId, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATED_LIKE_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findById(any());
    }

    @DisplayName("???????????? ???????????? ???????????? ?????? ??? ??????.")
    @Test
    void ?????????_?????????_??????() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //when
        LikeResponseDto responseDto = postService.like(post.getId(), authUser);

        //then
        assertAll(
                () -> assertThat(responseDto.getLikeCount()).isEqualTo(1),
                () -> assertThat(responseDto.getWriter()).isEqualTo(user.getUsername()),
                () -> assertThat(responseDto.getPostId()).isEqualTo(post.getId())
        );
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @DisplayName("???????????? ????????? ?????? ???????????? ?????? ???????????? ????????? ??? ??????.")
    @Test
    void ?????????_??????_??????() {
        //given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        Long postId = post.getId();

        //when then
        assertThatThrownBy(
                () -> postService.unlike(postId, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.LIKE_NOT_FOUND_EXCEPTION.getMessage());

        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findById(any());
    }

    @DisplayName("????????? ?????? ??????")
    @Test
    void ?????????_?????????_??????_??????() {
        //given
        post.like(user);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));


        //when
        LikeResponseDto responseDto = postService.unlike(post.getId(), authUser);

        //then
        assertAll(
                () -> assertThat(responseDto.getLikeCount()).isZero(),
                () -> assertThat(responseDto.getWriter()).isEqualTo(user.getUsername()),
                () -> assertThat(responseDto.getPostId()).isEqualTo(post.getId())
        );

        verify(postRepository, times(1)).findById(post.getId());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @DisplayName("????????? ?????? ??????")
    @Test
    void ?????????_??????_??????() {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tagService.findOrCreateTag(anyString())).thenReturn(tag);

        //when
        postService.update(updateDto, authUser);

        //then
        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tagService, times(updateDto.getTagNames().size())).findOrCreateTag(anyString());
    }

    @DisplayName("????????? ???????????? ?????? ???????????? ????????? ????????? ??? ??????.")
    @Test
    void ????????????_??????_?????????_?????????_??????_??????() {
        //given
        User notWriter = User.builder()
                .username("garam1234")
                .email("kgr@naver.com")
                .build();
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(notWriter));

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_WRITER_NOT_MATCH_EXCEPTION);

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @DisplayName("??????????????? ???????????? ????????? ????????? ????????????.")
    @Test
    void ???????????????_????????????_?????????_??????_??????() {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_NOT_FOUND_EXCEPTION);

        verify(userRepository, times(1)).findByUsername(any());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @DisplayName("??????????????? ???????????? ???????????? ???????????? ????????? ????????? ????????????.")
    @Test
    void ???????????????_?????????_????????????_?????????_??????_??????() {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @DisplayName("??????????????? ???????????? ?????? ????????? ???????????? ????????? ????????? ????????????.")
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", " "})
    @ParameterizedTest
    void ???????????????_?????????_????????????_??????_?????????_?????????_??????_??????(String tagName) {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tagName))
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tagService.findOrCreateTag(anyString())).thenThrow(new BusinessException(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION));

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);

        verify(postRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tagService, times(1)).findOrCreateTag(anyString());
    }
}
