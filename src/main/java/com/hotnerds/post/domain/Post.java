package com.hotnerds.post.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.post.domain.like.Like;
import com.hotnerds.post.domain.like.Likes;
import com.hotnerds.post.domain.comment.Comment;
import com.hotnerds.post.domain.comment.Comments;
import com.hotnerds.post.domain.tag.PostTags;
import com.hotnerds.tag.domain.Tag;
import com.hotnerds.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @Embedded
    private Comments comments;

    @Embedded
    Likes likes;

    @Embedded
    PostTags postTags;

    public boolean isWriter(User user) {
        return this.writer.equals(user);
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

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

    public void clearTag() {
        postTags.clear();
    }

    public List<String> getTagNames() {
        return postTags.getAllTagNames();
    }

    public int getLikeCount() {
        return likes.getCount();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Long commentId) {
        comments.remove(commentId);
    }

    public void updateComment(Long commentId, String content) {
        comments.update(commentId, content);
    }

    public List<Comment> getAllComments() {
        return comments.getComments();
    }

    public Post(String title, String content, User writer) {
        this(null, title, content, writer, null, Likes.empty(), PostTags.empty());
    }

    public Post(Long id, String title, String content, User writer) {
        this(id, title, content, writer, null, Likes.empty(), PostTags.empty());
    }

    @Builder
    public Post(Long id, String title, String content, User writer, Comments comments, Likes likes, PostTags postTags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.comments = comments;
        this.likes = likes;
        this.postTags = postTags;
    }
}
