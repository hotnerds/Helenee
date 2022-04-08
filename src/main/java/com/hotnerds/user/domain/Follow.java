package com.hotnerds.user.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "follow")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FOLLOWER_USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "FOLLOWED_USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User followed;

    @Builder
    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return this.getFollower().equals(follow.getFollower())
                && this.getFollowed().equals(follow.getFollowed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.follower, this.followed);
    }
}
