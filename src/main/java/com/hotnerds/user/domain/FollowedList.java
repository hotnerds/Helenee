package com.hotnerds.user.domain;

import com.hotnerds.user.exception.FollowRelationshipExistsException;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
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
            throw new FollowRelationshipExistsException("추가하려 하는 팔로우 관계가 이미 존재합니다");
        }
        followed.add(follow);
    }

}
