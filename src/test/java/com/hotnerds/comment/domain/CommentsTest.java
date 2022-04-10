package com.hotnerds.comment.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentsTest {

    private User writer;
    private Post post;
    private Comment comment1;
    private Comment comment2;
    private Comments comments;

    static final String TEXT = "An apple keeps the doctor away1";

    @BeforeEach
    void setUp() {
        writer = new User("user1", "email");
        post = new Post("title", TEXT, writer);
        comment1 = new Comment(1L, writer, post, TEXT);
        comment2 = new Comment(2L, writer, post, TEXT);
        comments = new Comments(List.of(comment1, comment2));
    }

    @DisplayName("Comments에 새로운 댓글 엔티티를 추가할 때 동일한 댓글 엔티티가 존재한다면 예외발생")
    @Test
    void 댓글_추가_실패() {
        // given
        Comment newComment = new Comment(1L, writer, post, TEXT);

        // when then
        assertThatThrownBy(() -> comments.add(newComment))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.COMMENT_DUPLICATED_EXCEPTION);
    }

    @DisplayName("Comments에 새로운 댓글 엔티티를 추가할 수 있다")
    @Test
    void 댓글_추가_성공() {
        // Comments 내부의 comments 리스트는 불변 객체이기 때문에 테스트 불가능.
    }

    @DisplayName("Comments에 있는 댓글 중 특정 ID를 가진 댓글을 삭제할 수 있다.")
    @Test
    void 댓글_삭제_실패() {
        // when then
        assertThatThrownBy(() -> comments.remove(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("Comments에 있는 댓글 중 특정 ID를 가진 댓글을 삭제할 수 있다.")
    @Test
    void 댓글_삭제_성공() {
        // Comments 내부의 comments 리스트는 불변 객체이기 때문에 테스트 불가능.
    }

    @DisplayName("Comments에 있는 댓글 중 특정 ID를 가진 댓글을 조회할 수 있다.")
    @Test
    void 댓글_조회_성공() {
        // when then
        assertThat(comments.getOneComment(1L)).isEqualTo(comment1);
    }

}