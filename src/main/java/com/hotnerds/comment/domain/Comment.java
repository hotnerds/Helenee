package com.hotnerds.comment.domain;

import javax.persistence.Entity;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "content")
    private String content;

    @Builder
    public Comment(Long id, User writer, Post post, String content) {
        this.id = id;
        this.writer = writer;
        this.post = post;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(this.getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.writer, this.post, this.content);
    }

    public void updateContent(String content) {
        if (!checkContentValid(content)) {
            throw new BusinessException(ErrorCode.COMMENT_INVALID_EXCEPTION);
        }
        this.content = content;
    }

    public static boolean checkContentValid(String content) {
        return !content.equals("") && !(content.length() > 1000);
    }
}
