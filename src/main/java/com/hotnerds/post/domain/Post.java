package com.hotnerds.post.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.post.domain.comment.Comment;
import com.hotnerds.post.domain.comment.Comments;
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
    private Comments comments;

    public void addComment(Comment comment) {
        this.getComments().add(comment);
    }

}
