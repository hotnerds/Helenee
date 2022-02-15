package com.hotnerds.user.domain.Dto;

import com.hotnerds.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateReqDto {
    private String username;

    @Builder
    public UserUpdateReqDto(String username) {
        this.username = username;
    }

    public User toEntity() {
        return User.builder()
                .username(this.username)
                .build();
    }
}
