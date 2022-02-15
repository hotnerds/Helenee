package com.hotnerds.user.domain;

import com.hotnerds.common.BaseTimeEntity;
import com.hotnerds.user.domain.Dto.UserUpdateReqDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @Column(nullable = false, length = 20, unique = true)
    private String email;

    public void updateUser(UserUpdateReqDto userUpdateReqDto) {
        this.username = userUpdateReqDto.getUsername();
    }

    @Builder
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
