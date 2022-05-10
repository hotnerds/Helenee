package com.hotnerds.post.application;

import com.hotnerds.IntegrationTest;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.common.security.oauth2.service.AuthProvider;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.*;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.tag.domain.repository.TagRepository;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PostServiceIntegrationTest extends IntegrationTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostService postService;

    @Autowired
    TagRepository tagRepository;

    User user;

    Post post;

    Tag tag;

    AuthenticatedUser authUser;

    @BeforeEach
    void init() {
        user = new User("garamkim", "kgr4163@korea.ac.kr", ROLE.USER, AuthProvider.KAKAO);
        post = new Post("title", "content", user);
        tag = new Tag("tagName");
        authUser = AuthenticatedUser.of(user);

    }

    @DisplayName("유저는 게시글을 등록할 수 있다.")
    @Test
    void 게시글_등록_성공() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        userRepository.save(user);

        //when
        Long postId = postService.write(requestDto, authUser);

        //then
        assertThat(postId).isNotNull();
    }

    @DisplayName("존재하지 않는 사용자는 게시글을 작성할 수 없다.")
    @Test
    void 존재하지_않은_사용자_게시글_작성_실패() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        //when then
        assertThatThrownBy(() -> postService.write(requestDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("유저는 유효하지 않는 태그 이름을 갖는 게시물을 생성할 수 없다.")
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", " "})
    @ParameterizedTest
    void 유효하지않는_태그를_갖는_게시글_생성_실패(String tagName) {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tagName))
                .build();

        userRepository.save(user);

        //when then
        assertThatThrownBy(() -> postService.write(requestDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);
    }

    @DisplayName("유저는 중복된 태그를 가진 게시물을 생성할 수 없다.")
    @Test
    void 중복된_태그를_가진_게시글_생성_실패() {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of("tagName", "tagName"))
                .build();

        userRepository.save(user);

        //when then
        assertThatThrownBy(() -> postService.write(requestDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.DUPLICATED_TAG_EXCEPTION);
    }

    @DisplayName("유저는 전체 게시글을 조회할 수 있다.")
    @Test
    void 전체_게시글_조회_성공() {
        //given
        PageRequest pageable = PageRequest.of(0, 10);
        userRepository.save(user);
        postRepository.save(post);

        //when
        List<PostResponseDto> findPosts = postService.searchAll(pageable);

        //then
        assertThat(findPosts).hasSize(1);
    }

    @DisplayName("유저는 게시글을 게시글 ID로 조회할 수 있다.")
    @Test
    void 게시글_ID_조회_성공() {
        //given
        userRepository.save(user);
        Post savedPost = postRepository.save(post);

        //when
        PostResponseDto postResponseDto = postService.searchByPostId(savedPost.getId());

        //then
        assertThat(postResponseDto.getPostId()).isEqualTo(savedPost.getId());
    }

    @DisplayName("해당하는 Id를 가진 게시글이 없으면 예외를 발생시킨다")
    @Test
    void 게시글_Id_없으면_예외_발생() {
        //given
        userRepository.save(user);
        postRepository.save(post);

        //when then
        assertThatThrownBy(() -> postService.searchByPostId(2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("유저는 게시글을 제목으로 조회할 수 있다.")
    @Test
    void 게시글_제목으로_조회() {
        //given
        userRepository.save(user);
        Post savedPost = postRepository.save(post);
        PostByTitleRequestDto requestDto = PostByTitleRequestDto.builder()
                .title(savedPost.getTitle())
                .pageable(PageRequest.of(0 ,10))
                .build();
        //when
        List<PostResponseDto> findPosts = postService.searchByTitle(requestDto);

        //then
        assertThat(findPosts).hasSize(1);
    }

    @DisplayName("존재하지 않은 사용자 게시물을 조회할 수 없다.")
    @Test
    void 존재하지_않은_사용자_게시물_조회_실패() {
        //given
        PostByWriterRequestDto requestDto = PostByWriterRequestDto.builder()
                .writer(user.getUsername())
                .pageable(PageRequest.of(0, 10))
                .build();
        //when then
        assertThatThrownBy(() -> postService.searchByWriter(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("유저는 게시글을 작성자 이름으로 조회할 수 있다.")
    @Test
    void 작성자_이름으로_게시물_조회() {
        //given
        userRepository.save(user);
        postRepository.save(post);

        PostByWriterRequestDto requestDto = PostByWriterRequestDto.builder()
                .writer(user.getUsername())
                .pageable(PageRequest.of(0, 10))
                .build();

        //when
        List<PostResponseDto> findResults = postService.searchByWriter(requestDto);

        //then
        assertThat(findResults).hasSize(1);
    }

    @DisplayName("유저는 태그를 이용해서 게시글을 조회할 수 있다.")
    @Test
    void 태그로_게시글_조회() {
        //given
        userRepository.save(user);
        tagRepository.save(tag);
        postRepository.save(post);
        post.addTag(tag);
        PostByTagRequestDto requestDto = PostByTagRequestDto.builder()
                .tagNames(List.of(tag.getName()))
                .pageable(PageRequest.of(0, 10))
                .build();
        //when
        List<PostResponseDto> findPosts = postService.searchByTagNames(requestDto);

        //then
        assertThat(findPosts).hasSize(1);
    }

    @DisplayName("유효하지 않은 태그 이름으로 게시글 조회 시 예외가 발생한다.")
    @Test
    void 유효하지_않은_태그이름_게시글_조회_실패() {
        //given
        userRepository.save(user);
        tagRepository.save(tag);
        postRepository.save(post);
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

    @DisplayName("존재하지 않은 사용자는 게시글을 삭제할 수 없다.")
    @Test
    void 유효하지않은사용자_게시글_삭제_실패() {
        assertThatThrownBy(() -> postService.delete(1L, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("유저는 존재하지 않은 게시글을 삭제 할 수 없다.")
    @Test
    void 존재하지않은_게시글_삭제_실패() {
        //given
        userRepository.save(user);
        Long postId = 1L;
        //when then
        assertThatThrownBy(() -> postService.delete(postId, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("게시글 작성자와 삭제 요청한 유저가 다르면 삭제 할 수 없다.")
    @Test
    void 게시글_작성자와_요청한_유저가_다르면_삭제_실패() {
        User otherUser = new User("otherUser", "aaa@aaa", ROLE.USER, AuthProvider.KAKAO);
        AuthenticatedUser otherAuthenticatedUser = AuthenticatedUser.of(otherUser);
        userRepository.save(user);
        userRepository.save(otherUser);
        postRepository.save(post);

        //when then
        assertThatThrownBy(() -> postService.delete(post.getId(), otherAuthenticatedUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_WRITER_NOT_MATCH_EXCEPTION.getMessage());
    }

    @DisplayName("유저는 게시글을 삭제할 수 있다.")
    @Test
    void 게시글_삭제_성공() {
        //given
        userRepository.save(user);
        postRepository.save(post);

        //when then
        postService.delete(post.getId(), authUser);
    }

    @DisplayName("존재하지 않는 게시글에 좋아요를 누르면 실패 한다.")
    @Test
    void 존재하지않은_게시글_좋아요_실패() {
        //given
        userRepository.save(user);
        Long postId = 1L;
        //when then
        assertThatThrownBy(
                () -> postService.like(postId, authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("존재하지 않는 사용자가 게시글에 좋아요를 요청하면 실패한다.")
    @Test
    void 존재하지않는_사용자_좋아요_실패() {
        assertThatThrownBy(
                () -> postService.like(post.getId(), authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("게시글 좋아요 요청이 중복되면 예외를 발생시킨다.")
    @Test
    void 게시글_좋아요_중복() {
        //given
        userRepository.save(user);
        post.like(user);
        postRepository.save(post);


        //when then
        assertThatThrownBy(
                () -> postService.like(post.getId(), authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATED_LIKE_EXCEPTION.getMessage());
    }

    @DisplayName("사용자가 게시물에 좋아요를 누를 수 있다.")
    @Test
    void 게시글_좋아요_성공() {
        //given
        userRepository.save(user);
        postRepository.save(post);

        //when
        LikeResponseDto responseDto = postService.like(post.getId(), authUser);

        //then
        assertAll(
                () -> assertThat(responseDto.getLikeCount()).isEqualTo(1),
                () -> assertThat(responseDto.getWriter()).isEqualTo(user.getUsername()),
                () -> assertThat(responseDto.getPostId()).isEqualTo(post.getId())
        );
    }

    @DisplayName("좋아요를 누르지 않은 게시물에 대해 좋아요를 취소할 수 없다.")
    @Test
    void 좋아요_취소_실패() {
        //given
        userRepository.save(user);
        postRepository.save(post);

        //when then
        assertThatThrownBy(
                () -> postService.unlike(post.getId(), authUser))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.LIKE_NOT_FOUND_EXCEPTION.getMessage());
    }

    @DisplayName("유저는 좋아요를 취소할 수 있다.")
    @Test
    void 게시글_좋아요_취소_성공() {
        //given
        userRepository.save(user);
        post.like(user);
        postRepository.save(post);

        //when
        LikeResponseDto responseDto = postService.unlike(post.getId(), authUser);

        //then
        assertAll(
                () -> assertThat(responseDto.getLikeCount()).isEqualTo(0),
                () -> assertThat(responseDto.getWriter()).isEqualTo(user.getUsername()),
                () -> assertThat(responseDto.getPostId()).isEqualTo(post.getId())
        );
    }

    @DisplayName("유저는 게시글을 수정할 수 있다.")
    @Test
    void 게시글_수정_성공() {
        //given
        userRepository.save(user);
        tagRepository.save(tag);
        post.addTag(tag);
        postRepository.save(post);

        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        //when
        postService.update(updateDto, authUser);
    }

    @DisplayName("게시글 작성자가 아닌 사용자는 게시글 수정할 수 없다.")
    @Test
    public void 작성자가_아닌_사용자_게시글_수정_실패() {
        //given
        User otherUser = new User("otherUser", "aaa@aaa", ROLE.USER, AuthProvider.KAKAO);
        AuthenticatedUser otherAuthenticatedUser = AuthenticatedUser.of(otherUser);
        userRepository.save(user);
        userRepository.save(otherUser);
        tagRepository.save(tag);
        post.addTag(tag);
        postRepository.save(post);

        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, otherAuthenticatedUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_WRITER_NOT_MATCH_EXCEPTION);
    }

    @DisplayName("수정하려는 게시글이 없으면 예외가 발생한다.")
    @Test
    void 수정하려는_게시글이_없을때_예외_발생() {
        //given
        userRepository.save(user);
        Long postId = 1L;

        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(postId)
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.POST_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("수정하려는 게시글의 사용자가 존재하지 않으면 예외가 발생한다.")
    @Test
    void 수정하려는_게시글_사용자가_없을때_예외_발생() {
        //given
        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tag.getName()))
                .build();

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("수정하려는 게시글에 태그 이름이 유효하지 않으면 예외가 발생한다.")
    @ValueSource(strings = {"", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", " "})
    @ParameterizedTest
    void 수정하려는_게시글_유효하지_않은_태그를_갖으면_예외_발생(String tagName) {
        //given
        userRepository.save(user);
        postRepository.save(post);

        PostUpdateRequestDto updateDto = PostUpdateRequestDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tagNames(List.of(tagName))
                .build();

        //when then
        assertThatThrownBy(() -> postService.update(updateDto, authUser))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);
    }
}

