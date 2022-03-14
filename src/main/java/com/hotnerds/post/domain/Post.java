package com.hotnerds.post.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.post.domain.like.Like;
import com.hotnerds.post.domain.like.Likes;
import com.hotnerds.user.domain.User;
import lombok.*;
import org.hibernate.mapping.Join;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Embedded
    Likes likes;

    public void like(User user) {
        Like like = Like.builder()
                .id(null)
                .post(this)
                .user(user)
                .build();
        likes.add(like);
    }

    public void unlike(User user) {
        Like like = Like.builder()
                .id(null)
                .post(this)
                .user(user)
                .build();
        likes.remove(like);
    }

    public int getLikeCount() {
        return likes.getCount();
    }

}
