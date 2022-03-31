package com.hotnerds.user.domain.dto;

import com.hotnerds.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String username;
    private String email;

    @Builder
    public UserResponseDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
