package com.hotnerds.user.domain.DTO;

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

    public NewUserDto of(User user) {
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
