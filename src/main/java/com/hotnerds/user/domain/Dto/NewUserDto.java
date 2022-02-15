package com.hotnerds.user.domain.Dto;

import com.hotnerds.user.domain.User;
import lombok.*;

@Getter
@NoArgsConstructor
public class NewUserDto {
    private String username;
    private String email;

    @Builder
    public NewUserDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static NewUserDto of(User user) {
        return NewUserDto.builder()
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
