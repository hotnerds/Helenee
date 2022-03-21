package com.hotnerds.post.domain.comment;

import com.hotnerds.post.domain.Post;
import com.hotnerds.post.exception.CommentExistsException;
import com.hotnerds.post.exception.CommentNotFoundException;
import com.hotnerds.user.domain.User;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Comments {
    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    public Comments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean contains(Comment comment) {
        return this.getComments().stream()
                .anyMatch(comment::equals);
    }

    public Comment add(Comment comment) {
        if (comments.contains(comment)) {
            throw new CommentExistsException();
        }
        comments.add(comment);
        return comment;
    }

    public void remove(Long commentId) {
        Comment comment = comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findAny()
                .orElseThrow(CommentNotFoundException::new);
        comments.remove(comment);
    }
}
