package com.hotnerds.user.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.user.domain.dto.UserUpdateReqDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @Column(nullable = false, length = 20, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    ROLE role;

    @OneToMany
    @JoinColumn(name = "DIET_ID")
    private List<Diet> dietList;

    @Embedded
    private FollowerList followerList;

    @Embedded
    private FollowedList followedList;

    @Builder
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.followerList = new FollowerList();
        this.followedList = new FollowedList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.getClass() != o.getClass()) return false;
        User anotherUserEntity = (User) o;
        return this.username.equals(anotherUserEntity.getUsername()) &&
                this.email.equals(anotherUserEntity.getEmail());
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int hashCode = 1;
        hashCode = PRIME * hashCode + ((this.getUsername() == null) ? 0 : this.getUsername().hashCode());
        hashCode = PRIME * hashCode + ((this.getEmail() == null) ? 0 : this.getEmail().hashCode());
        return hashCode;
    }

    public User updateUser(UserUpdateReqDto userUpdateReqDto) {
        this.username = userUpdateReqDto.getUsername();
        return this;
    }

    public void follow(User followed) {
        Follow newFollow = new Follow(this, followed);
        this.getFollowedList().add(newFollow);
        followed.getFollowerList().add(newFollow);
    }

    public boolean isFollowerOf(User followed) {
        return this.getFollowedList().isFollowing(followed);
    }

    public boolean isFollowedBy(User follower) {
        return this.getFollowerList().isFollowedBy(follower);
    }

    public User(String username, String email, ROLE role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public void unfollow(User followed) {
        Follow newFollow = new Follow(this, followed);
        this.getFollowedList().delete(newFollow);
        followed.getFollowerList().delete(newFollow);
    }
}