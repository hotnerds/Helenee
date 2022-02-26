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
@Embeddable
public class FollowerId implements Serializable {

    // Logic : FOLLOWER_ID is following FOLLOWING_ID

    @Column(name = "FOLLOWER_ID", nullable = false)
    private Long followerID;

    @Column(name = "FOLLOWING_ID", nullable = false)
    private Long followingID;
}
