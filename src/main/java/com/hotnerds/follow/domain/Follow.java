package com.hotnerds.follow.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "follower")
public class Follow {

    @EmbeddedId
    private FollowId followId;

}