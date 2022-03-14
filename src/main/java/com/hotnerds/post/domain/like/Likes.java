package com.hotnerds.post.domain.like;

import com.hotnerds.post.exception.DuplicatedLikeException;
import com.hotnerds.user.domain.User;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Likes {
    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<Like> likes = new ArrayList<>();

    public int getCount() {
        return likes.size();
    }

    public boolean contains(Like like) {
        return likes.stream()
                .anyMatch(like::equals);
    }

    public void add(Like like) {
        if(likes.contains(like)) {
            throw new DuplicatedLikeException();
        }
        likes.add(like);
    }

    public void remove(Like like) {
        if(!likes.contains(like)) {

        }
        likes.remove(like);
    }

    public List<User> getLikesUsers() {
        return likes.stream()
                .map(like -> like.getUser())
                .collect(Collectors.toList());
    }

    public Likes(List<Like> likes) {
        this.likes = likes;
    }
}