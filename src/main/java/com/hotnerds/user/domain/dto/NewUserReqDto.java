package com.hotnerds.user.domain.dto;

import com.hotnerds.user.domain.User;
import lombok.*;

@Getter
@NoArgsConstructor
public class NewUserReqDto {
    private String username;
    private String email;

    @Builder
    public NewUserReqDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static NewUserReqDto of(User user) {
        return NewUserReqDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .username(this.getUsername())
                .email(this.getEmail())
                .build();
    }
}
