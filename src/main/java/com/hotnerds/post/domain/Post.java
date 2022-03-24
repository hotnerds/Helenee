package com.hotnerds.post.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.post.domain.like.Like;
import com.hotnerds.post.domain.like.Likes;
import com.hotnerds.post.domain.comment.Comment;
import com.hotnerds.post.domain.comment.Comments;
import com.hotnerds.user.domain.User;
import lombok.*;
import org.hibernate.mapping.Join;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

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
    private Comments comments;

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
