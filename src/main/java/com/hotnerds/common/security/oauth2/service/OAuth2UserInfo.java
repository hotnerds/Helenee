package com.hotnerds.common.security.oauth2.service;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@Slf4j
public class OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final String name;
    private final String email;

    @Builder
    public OAuth2UserInfo(Map<String, Object> attributes, String name, String email) {
        this.attributes = attributes;
        this.name = name;
        this.email = email;
    }

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.KAKAO.getRegistrationId()))
            return ofKakao(attributes);

        throw new BusinessException(ErrorCode.AUTHENTICATION_PROVIDER_NOT_FOUND);
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

        return OAuth2UserInfo.builder()
                .name(String.valueOf(properties.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .attributes(attributes)
                .build();
    }


}