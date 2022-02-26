package com.hotnerds.followers.domain;

import com.hotnerds.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "follower")
public class Follower {

    @EmbeddedId
    private FollowerId followerId;

}