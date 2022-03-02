package com.hotnerds.follow.domain;

import com.hotnerds.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "follower")
public class Follow {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "USER_ID", name = "FOLLOWER_ID")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "USER_ID", name = "FOLLOWING_ID")
    private User following;

    @Override
    public boolean equals(Object anotherFollowRelationship) {
        if (this == anotherFollowRelationship) return true;
        return Objects.equals(follower, ((Follow) anotherFollowRelationship).follower) &&
                Objects.equals(following, ((Follow) anotherFollowRelationship).following);
    }

}