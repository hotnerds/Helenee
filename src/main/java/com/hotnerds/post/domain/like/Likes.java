package com.hotnerds.post.domain.like;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
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
            throw new BusinessException(ErrorCode.DUPLICATED_LIKE_EXCEPTION);
        }
        likes.add(like);
    }

    public void remove(Like like) {
        if(!likes.contains(like)) {
            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND_EXCEPTION);
        }
        likes.remove(like);
    }

    public List<User> getLikesUsers() {
        return likes.stream()
                .map(like -> like.getUser())
                .collect(Collectors.toList());
    }

    public static Likes empty() {
        return new Likes(new ArrayList<>());
    }
}
