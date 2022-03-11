package com.hotnerds.user.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "follow")
public class Follow {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FOLLOWER_USER_ID")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "FOLLOWED_USER_ID")
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
        return this.getId().equals(follow.getId());
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int hashCode = 1;
        hashCode = PRIME * hashCode + getId().intValue();
        return hashCode;
    }
}
