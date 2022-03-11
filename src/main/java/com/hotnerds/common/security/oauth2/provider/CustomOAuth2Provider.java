package com.hotnerds.common.security.oauth2.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.function.Function;

public enum CustomOAuth2Provider {
    KAKAO {
        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_POST,
                    DEFAULT_LOGIN_REDIRECT_URL)
                    .scope("profile_nickname", "account_email")
                    .authorizationUri(AUTHORIZATION_URI)
                    .tokenUri(TOKEN_URI)
                    .userInfoUri(USER_INFO_URI)
                    .userNameAttributeName("id")
                    .clientName("Kakao");

            return builder;

        }
    };

    protected final ClientRegistration.Builder getBuilder(String registrationId, ClientAuthenticationMethod method,
                                                          String redirectUri) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId)
                .clientAuthenticationMethod(method)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri);

        return builder;
    }

    private static final String DEFAULT_LOGIN_REDIRECT_URL = "http://localhost:8080/login/oauth2/code/kakao";
    private static final String AUTHORIZATION_URI = "https://kauth.kakao.com/oauth/authorize";
    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    public abstract ClientRegistration.Builder getBuilder(String registrationId);
}