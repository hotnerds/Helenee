package com.hotnerds.post.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.post.domain.like.Like;
import com.hotnerds.post.domain.like.Likes;
import com.hotnerds.post.domain.tag.PostTags;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.user.domain.User;
import lombok.*;
import org.hibernate.mapping.Join;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Embedded
    PostTags postTags;

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

    public void addTag(Tag tag) {
        postTags.addTag(this, tag);
    }

    public void removeTag(Tag tag) {
        postTags.removeTag(tag);
    }

    public int getLikeCount() {
        return likes.getCount();
    }

    public Post(String title, String content, User writer) {
        this(null, title, content, writer, Likes.empty(), PostTags.empty());
    }

    public Post(Long id, String title, String content, User writer) {
        this(id, title, content, writer, Likes.empty(), PostTags.empty());
    }

    @Builder
    public Post(Long id, String title, String content, User writer, Likes likes, PostTags postTags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.likes = likes;
        this.postTags = postTags;
    }
}
