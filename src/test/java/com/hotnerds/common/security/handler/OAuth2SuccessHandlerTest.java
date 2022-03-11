package com.hotnerds.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.security.oauth2.provider.JwtTokenProvider;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    private static final ROLE USER_ROLE = ROLE.USER;
    private static final String NAME_ATTRIBUTE_KEY = "id";

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    OAuth2SuccessHandler oAuth2SuccessHandler;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    PrintWriter printWriter;

    @Mock
    Authentication authentication;

    @Spy
    ObjectMapper objectMapper;

    private OAuth2User oAuth2User;

    private Map<String, Object> attributes;

    @BeforeEach
    void setup() {
        attributes = new HashMap<>();

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "kgr4163@naver.com");

        attributes.put("kakao_account", kakaoAccount);
        attributes.put(NAME_ATTRIBUTE_KEY, "1L");

        oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(USER_ROLE.getKey())),
                attributes, NAME_ATTRIBUTE_KEY);
    }

    @DisplayName("인증에는 성공했으나 해당하는 유저가 존재하지 않는다.")
    @Test
    void 인증_성공했으나_유저_존재하지않음() throws IOException, ServletException {
        //given
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("회원을 찾지 못하였습니다.");
    }

    @DisplayName("인증 성공 후 정상적으로 토큰을 발급한다.")
    @Test
    void 인증_성공후_토큰_발급() throws IOException, ServletException{
        //given
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(User.builder().build()));
        when(jwtTokenProvider.createToken(anyString())).thenReturn("token");
        when(response.getWriter()).thenReturn(printWriter);

        //when then
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);
    }
}