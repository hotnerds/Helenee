package com.hotnerds.user.domain.dto;

import com.hotnerds.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowUserInfoResponseDto {
    private Long userId;
    private String username;
    private String email;

    public static FollowUserInfoResponseDto of(final User user) {
        return new FollowUserInfoResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
