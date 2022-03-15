package com.hotnerds.user.domain;

import com.hotnerds.user.exception.FollowRelationshipExistsException;
import com.hotnerds.user.exception.FollowRelationshipNotFound;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
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
            throw new FollowRelationshipExistsException("추가하려 하는 팔로우 관계가 이미 존재합니다");
        }
        followers.add(follow);
    }

    public void delete(Follow follow) {
        if (!followers.contains(follow)) {
            throw new FollowRelationshipNotFound();
        }
        followers.remove(follow);
    }
}
