package com.hotnerds.post.domain.comment;

import javax.persistence.Entity;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Embeddable
public class Comment extends BaseTimeEntity {
    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User writer;

    @ManyToOne
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
        return Objects.equals(this.getId(), comment.getId())
                && Objects.equals(this.getWriter(), comment.getWriter())
                && Objects.equals(this.getPost(), comment.getPost())
                && Objects.equals(this.getContent(), comment.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(writer, post, content);
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
