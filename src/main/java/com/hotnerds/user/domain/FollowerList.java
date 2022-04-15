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
public class FollowerList {
    @OneToMany(mappedBy = "follower", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Follow> followers = new ArrayList<>();

    public boolean isFollowedBy(User user) {
        return followers.stream()
                .anyMatch(f -> f.getFollower().equals(user));
    }

    public int followerCounts() {
        return followers.size();
    }

    public void add(Follow follow) {
        if (followers.contains(follow)) {
            throw new BusinessException(ErrorCode.FOLLOW_DUPLICATED_EXCEPTION);
        }
        followers.add(follow);
    }

    public void delete(Follow follow) {
        if (!followers.contains(follow)) {
            throw new BusinessException(ErrorCode.FOLLOW_NOT_FOUND_EXCEPTION);
        }
        followers.remove(follow);
    }
}