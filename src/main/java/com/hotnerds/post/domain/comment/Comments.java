package com.hotnerds.post.domain.comment;

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

    public boolean contains(Comment expectedComment) {
        return this.getComments().stream()
                .anyMatch(c -> c.equals(expectedComment));
    }

    public Comment add(Comment comment) {
        comments.add(comment);
        return comment;
    }
}
