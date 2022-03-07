package com.hotnerds.user.domain;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
@Getter
public class FollowedList {

    @OneToMany(mappedBy = "user")
    private List<Follow> followed;

    public boolean isFollowing(User user) {
        return followed.stream()
                .anyMatch(f -> f.getFollower().equals(user));
    }

    public int followCounts() {
        return followed.size();
    }

}
