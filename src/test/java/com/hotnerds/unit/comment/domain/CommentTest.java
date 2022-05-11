package com.hotnerds.unit.comment.domain;

import com.hotnerds.comment.domain.Comment;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    private User writer;
    private Post post;
    private Comment comment;
    static final String TEXT = "An apple keeps the doctor away";

    @BeforeEach
    void setUp() {
        writer = new User("user1", "email");
        post = new Post("title", TEXT, writer);
        comment = new Comment(1L, writer, post, TEXT);
    }

    @DisplayName("만약 equals 메서드의 파라미터로 자기자신을 받으면 true를 반환한다.")
    @Test
    void equals_본인_받음() {
        // when then
        assertThat(comment.equals(comment)).isTrue();
    }

    @DisplayName("만약 equals 메서드의 파라미터로 받은 객체의 Comment가 아니면 false를 반환한다")
    @Test
    void equals_다른_객체_받음() {
        // when then
        assertThat(comment.equals(post)).isFalse();
    }

    @DisplayName("만약 equals 메서드의 파라미터로 받은 Comment 객체의 id가 메서드 주인의 id와 다르다면 false를 반환한다.")
    @Test
    void equals_틀림() {
        // when then
        assertThat(comment.equals(new Comment(2L, writer, post, TEXT))).isFalse();
    }

    @DisplayName("만약 equals 메서드의 파라미터로 받은 Comment 객체의 id가 주인의 id와 같다면 true를 반환한다.")
    @Test
    void equals_맞음() {
        // when then
        assertThat(comment.equals(new Comment(1L, writer, post, TEXT))).isTrue();
    }

    @DisplayName("동일한 Comment 객체 두개는 동일한 hashCode 값을 가져야 한다")
    @Test
    void hashCode_테스트() {
        // given
        Comment newComment = new Comment(1L, writer, post, TEXT);

        // when then
        assertEquals(comment.hashCode(), newComment.hashCode());
    }

    @DisplayName("댓글의 내용의 수정을 요청할 때 수정될 내용이 공백이거나 길이가 1000 이상이라면 예외가 발생한다.")
    @Test
    void 댓글_수정_실패() {
        // given
        String tempStr = "";
        for (int i = 0; i < 1001; i++) {
            tempStr += "a";
        }
        final String TOO_LONG_CONTENT = tempStr;
        final String NO_CONTENT = "";

        // when then
        assertThatThrownBy(() -> comment.updateContent(TOO_LONG_CONTENT))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.COMMENT_INVALID_EXCEPTION);
        assertThatThrownBy(() -> comment.updateContent(NO_CONTENT))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.COMMENT_INVALID_EXCEPTION);
    }


}