package com.hotnerds.followers.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follower")
@IdClass(FollowerId.class)
public class Follower {
    @Id
    @Column(name = "FOLLOWER_ID")
    private Long followerID;

    @Id
    @Column(name = "FOLLOWING_ID")
    private Long followingID;

    @Builder
    public Follower(Long followerID, Long followingID) {
        this.followerID = followerID;
        this.followingID = followingID;
    }

}