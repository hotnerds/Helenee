package com.hotnerds.user.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
public class FollowedList {
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Follow> followed = new ArrayList<>();

    public boolean isFollowing(User user) {
        return followed.stream()
                .anyMatch(f -> f.getFollowed().equals(user));
    }

    public int followCounts() {
        return followed.size();
    }

    public void add(Follow follow) {
        followed.add(follow);
    }

}