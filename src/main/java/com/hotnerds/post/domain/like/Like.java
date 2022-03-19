package com.hotnerds.post.domain.like;

import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Table(name = "LIKES")
public class Like {
    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Like(Long id, User user, Post post) {
        this.id = id;
        this.user = user;
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(user, like.user) && Objects.equals(post, like.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post);
    }
}
