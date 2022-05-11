package com.hotnerds.common.security.oauth2.service;

import com.hotnerds.common.security.oauth2.provider.CustomOAuth2Provider;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    private static final ROLE USER_ROLE = ROLE.USER;
    private static final String NAME_ATTRIBUTE_KEY = "id";

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private DefaultOAuth2UserService defaultOAuth2UserService;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    private OAuth2User oAuth2User;

    private Map<String, Object> attributes;

    private User user;

    private CustomOAuth2Provider customOAuth2Provider;

    private ClientRegistration clientRegistration;

    @BeforeEach
    void setup() {
        attributes = new HashMap<>();

        Map<String, Object> properties = new HashMap<>();
        properties.put("nickname", "garam");

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "kgr4163@naver.com");

        attributes.put("properties", properties);
        attributes.put("kakao_account", kakaoAccount);
        attributes.put(NAME_ATTRIBUTE_KEY, NAME_ATTRIBUTE_KEY);

        oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(USER_ROLE.getKey())),
                attributes, NAME_ATTRIBUTE_KEY);

        user = new User("garam", "kgr4163@naver.com", ROLE.USER);

        clientRegistration = CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId("aaaaa")
                .build();
    }

    @DisplayName("이미 가입된 사용자가 있을 때 유저 정보 가져오기")
    @Test
    void 이미_가입된_유저_정보_가져오기() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(defaultOAuth2UserService.loadUser(userRequest)).thenReturn(oAuth2User);
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);

        //when
        OAuth2User authenticatedUser = customOAuth2UserService.loadUser(userRequest);

        //then
        assertAll(
                () -> assertThat(authenticatedUser.getName()).isEqualTo(NAME_ATTRIBUTE_KEY),
                () -> assertThat(authenticatedUser.getAttributes()).containsExactlyEntriesOf(attributes),
                () -> assertThat(authenticatedUser.getAuthorities()).allMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(USER_ROLE.getKey()))
        );
    }

    @DisplayName("신규 유저 회원 가입 후 정보 가져오기")
    @Test
    void 신규_유저_가입후_정보_가져오기() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(defaultOAuth2UserService.loadUser(userRequest)).thenReturn(oAuth2User);
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);

        //when
        OAuth2User authenticatedUser = customOAuth2UserService.loadUser(userRequest);

        //then
        assertAll(
                () -> assertThat(authenticatedUser.getName()).isEqualTo(NAME_ATTRIBUTE_KEY),
                () -> assertThat(authenticatedUser.getAttributes()).containsExactlyEntriesOf(attributes),
                () -> assertThat(authenticatedUser.getAuthorities()).allMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(USER_ROLE.getKey()))
        );
    }
}