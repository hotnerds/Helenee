package com.hotnerds.post.domain.like;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {
    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<Like> likeList = new ArrayList<>();

    public int getCount() {
        return likeList.size();
    }

    public void add(Like like) {
        if(likeList.contains(like)) {
            throw new BusinessException(ErrorCode.DUPLICATED_LIKE_EXCEPTION);
        }
        likeList.add(like);
    }

    public void remove(Like like) {
        if(!likeList.contains(like)) {
            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND_EXCEPTION);
        }
        likeList.remove(like);
    }

    public static Likes empty() {
        return new Likes(new ArrayList<>());
    }
}
