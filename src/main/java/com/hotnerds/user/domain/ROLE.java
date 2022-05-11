package com.hotnerds.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ROLE {
    USER("ROLE_USER", "사용자");

    private final String key;
    private final String title;
}
