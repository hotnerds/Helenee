package com.hotnerds.unit.post.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostTest {

    Post post;
    User user;
    Tag tag;

    @BeforeEach
    void init() {
        user = new User("garam", "kgr4163@korea.ac.kr");
        post = new Post("title", "content", user);
        tag = new Tag("tag");
    }

    @DisplayName("게시글에 유저는 좋아요를 누를 수 있다.")
    @Test
    void 게시글에_좋아요_추가_성공() {
        post.like(user);

        assertThat(post.getLikeCount()).isEqualTo(1);
    }

    @DisplayName("게시글에 중복된 유저가 좋아요를 누를 수 없다.")
    @Test
    void 중복된_게시글_좋아요_실패() {
        post.like(user);
        assertThatThrownBy(() -> post.like(user))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.DUPLICATED_LIKE_EXCEPTION);
    }

    @DisplayName("게시글에서 좋아요를 취소할 수 있다.")
    @Test
    void 게시글_좋아요_취소_성공() {
        //given
        post.like(user);

        //when
        post.unlike(user);

        //then
        assertThat(post.getLikeCount()).isEqualTo(0);
    }

    @DisplayName("좋아요를 누른 적 없는 유저가 좋아요를 취소할 수 없다.")
    @Test
    void 좋아요누른적없는유저_좋아요_취소_실패() {
        assertThatThrownBy(() -> post.unlike(user))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.LIKE_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("게시글에 태그를 추가할 수 있다.")
    @Test
    void 태그_추가_성공() {
        post.addTag(tag);

        assertThat(post.getPostTags().getPostTags().size()).isEqualTo(1);
    }

    @DisplayName("게시글은 중복된 태그를 가질 수 없다.")
    @Test
    void 중복된_태그_실패() {
        post.addTag(tag);

        assertThatThrownBy(() -> post.addTag(tag))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.DUPLICATED_TAG_EXCEPTION);
    }

    @DisplayName("게시글 붙은 태그를 삭제할 수 있다.")
    @Test
    void 태그_삭제_성공() {
        post.addTag(tag);

        post.removeTag(tag);

        assertThat(post.getPostTags().getPostTags().size()).isEqualTo(0);
    }

    @DisplayName("게시글에 붙어 있지 않은 태그를 지울 수는 없다.")
    @Test
    void 붙어있지않은_태그_삭제_실패() {
        assertThatThrownBy(() -> post.removeTag(tag))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .usingRecursiveComparison()
                .isEqualTo(ErrorCode.TAG_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("게시글 작성자 인지 확인한다.")
    @Test
    void 게시글_작성자_확인() {
        assertThat(post.isWriter(user)).isTrue();
    }
}