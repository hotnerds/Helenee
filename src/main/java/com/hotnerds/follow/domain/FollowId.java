package com.hotnerds.follow.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class FollowId implements Serializable {

    // Logic : FOLLOWER_ID is following FOLLOWING_ID

    @Column(name = "FOLLOWER_ID", nullable = false)
    private Long followerID;

    @Column(name = "FOLLOWING_ID", nullable = false)
    private Long followingID;

    @Builder
    public FollowId(Long followerID, Long followingID) {
        this.followerID = followerID;
        this.followingID = followingID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowId followIdObj = (FollowId) o;
        return this.followerID == followIdObj.followerID
                && this.followingID == followIdObj.followingID;
    }

}
