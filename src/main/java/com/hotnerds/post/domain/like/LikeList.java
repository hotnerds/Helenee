package com.hotnerds.post.domain.like;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeList {
    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<Likes> likeList = new ArrayList<>();

    public int getCount() {
        return likeList.size();
    }

    public boolean contains(Likes like) {
        return likeList.stream()
                .anyMatch(like::equals);
    }

    public void add(Likes like) {
        if(likeList.contains(like)) {
            throw new BusinessException(ErrorCode.DUPLICATED_LIKE_EXCEPTION);
        }
        likeList.add(like);
    }

    public void remove(Likes like) {
        if(!likeList.contains(like)) {
            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND_EXCEPTION);
        }
        likeList.remove(like);
    }

    public List<User> getLikesUsers() {
        return likeList.stream()
                .map(Likes::getUser)
                .collect(Collectors.toList());
    }

    public static LikeList empty() {
        return new LikeList(new ArrayList<>());
    }
}
