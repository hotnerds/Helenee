package com.hotnerds.common.security.oauth2.service;

import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthenticatedUser {
    private final Long id;
    private final String username;
    private final ROLE role;

    @Builder
    public AuthenticatedUser(Long id, String username, ROLE role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public static AuthenticatedUser of(User user) {
        return AuthenticatedUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
