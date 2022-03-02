package com.hotnerds.user.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.diet.domain.Diet;
import com.hotnerds.follow.domain.Follow;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
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

    @OneToMany
    @JoinColumn(name = "DIET_ID")
    private List<Diet> dietList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLLOWER_ID")
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLLOWING_ID")
    private List<Follow> followingList = new ArrayList<>();

    public void updateUser(UserUpdateReqDto userUpdateReqDto) {
        this.username = userUpdateReqDto.getUsername();
    }

    @Builder
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public boolean equals(User anotherUserEntity) {
        if (this == anotherUserEntity) return true;
        return this.username.equals(anotherUserEntity.getUsername()) &&
                this.email.equals(anotherUserEntity.getEmail());
    }
}
