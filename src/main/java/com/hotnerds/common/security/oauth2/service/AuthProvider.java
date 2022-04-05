package com.hotnerds.common.security.oauth2.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    KAKAO("kakao");

    private final String registrationId;
}
