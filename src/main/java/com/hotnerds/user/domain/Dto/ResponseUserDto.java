package com.hotnerds.user.domain.Dto;

import com.hotnerds.user.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
public class ResponseUserDto {
    private String username;
    private String email;

    @Builder
    public ResponseUserDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static ResponseUserDto of(User user) {
        return ResponseUserDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
