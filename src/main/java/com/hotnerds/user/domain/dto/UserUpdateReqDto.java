package com.hotnerds.user.domain.dto;

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
}
