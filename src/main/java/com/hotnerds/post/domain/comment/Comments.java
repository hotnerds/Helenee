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

    public Comment add(Comment comment) {
        comments.add(comment);
        return comment;
    }

}
