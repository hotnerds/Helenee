package com.hotnerds.common.security.oauth2.provider;

import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.common.security.oauth2.service.AuthProvider;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserRepository userRepository;

    @Value("${spring.security.jwt.token.secret-key}")
    private String jwtSecret;

    @Value("${spring.security.jwt.token.expire-length}")
    private Long expireLength;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        secretKey = Keys.hmacShaKeyFor(this.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String payload) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date expiredTime = new Date(now.getTime() + expireLength);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredTime)
                .signWith(secretKey)
                .compact();
    }

   public String getPayload(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }

    public Authentication getAuthentication(String token) {
        String payload = getPayload(token);
        User user = userRepository.findByEmail(payload).orElseThrow(
                () -> new AuthenticationCredentialsNotFoundException(ErrorCode.AUTHENTICATION_CREDENTIAL_NOT_FOUND.getMessage()));
;
        Map<String, Object> attributes = getAttributes(user);

        DefaultOAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())), attributes, "id");

        return new OAuth2AuthenticationToken(oAuth2User,oAuth2User.getAuthorities() , AuthProvider.KAKAO.getRegistrationId());
    }

    public Map<String, Object> getAttributes(User user) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("email", user.getEmail());
        return attributes;
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public boolean validateToken(String token) {
        try {
            Jwt<Header, Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJwt(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthenticationCredentialsNotFoundException(e.getMessage());
        }
    }

}
