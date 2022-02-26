package com.hotnerds.followers.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowerId implements Serializable {
    @Column(name = "FOLLOWER_ID")
    private Long followerID;

    @Column(name = "FOLLOWING_ID")
    private Long followingID;
}
