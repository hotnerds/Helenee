package com.hotnerds.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.common.security.oauth2.provider.JwtTokenProvider;
import com.hotnerds.user.domain.repository.UserRepository;
import com.nimbusds.jose.util.StandardCharset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");


        String email = String.valueOf((String)kakao_account.get("email"));

        userRepository.findByEmail(email).orElseThrow(
                () -> new AuthenticationCredentialsNotFoundException("회원을 찾지 못하였습니다."));

        String token = jwtTokenProvider.createToken(email);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("utf-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(token));

    }
}
