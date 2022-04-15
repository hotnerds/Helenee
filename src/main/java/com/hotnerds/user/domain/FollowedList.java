package com.hotnerds.user.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowedList {
    @OneToMany(mappedBy = "followed", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Follow> followed = new ArrayList<>();

    public boolean isFollowing(User user) {
        return followed.stream()
                .anyMatch(f -> f.getFollowed().equals(user));
    }

    public int followCounts() {
        return followed.size();
    }

    public void add(Follow follow) {
        if (followed.contains(follow)) {
            throw new BusinessException(ErrorCode.FOLLOW_DUPLICATED_EXCEPTION);
        }
        followed.add(follow);
    }

    public void delete(Follow follow) {
        if (!followed.contains(follow)) {
            throw new BusinessException(ErrorCode.FOLLOW_NOT_FOUND_EXCEPTION);
        }
        followed.remove(follow);
    }
}
