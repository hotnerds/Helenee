package com.hotnerds.followers.domain;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "follower")
public class Follower {
    @EmbeddedId
    private FollowerFollowingId followerFollowingId;
}