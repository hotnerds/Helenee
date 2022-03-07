package com.hotnerds.user.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Embeddable
@Getter
public class FollowerList {
    @OneToMany(mappedBy = "user")
    private List<Follow> followers;

    public boolean isFollowedBy(User user) {
        return followers.stream()
                .anyMatch(f -> f.getFollower().equals(user));
    }

    public int followerCounts() {
        return followers.size();
    }

}
