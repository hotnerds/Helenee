package com.hotnerds.common;

import com.hotnerds.common.security.oauth2.provider.CustomOAuth2Provider;
import com.hotnerds.common.security.oauth2.resolver.AuthenticatedUserMethodArgumentResolver;
import com.hotnerds.common.security.oauth2.service.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class OAuth2Config implements WebMvcConfigurer {

    private final AuthenticatedUserMethodArgumentResolver authenticatedUserMethodArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedUserMethodArgumentResolver);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${spring.security.oauth2.client.registration.kakao.clientId}") String kakaoClientId) {
        List<ClientRegistration> registrations = new ArrayList<>();

        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder(AuthProvider.KAKAO.getRegistrationId())
                .clientId(kakaoClientId)
                //.clientSecret(kakaoClientSecret)
                .build()); // 카카오는 JWT, JWK 방식으로 주고 받지 않으므로 jwtUri설정 안함.
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public DefaultOAuth2UserService defaultOAuth2UserService() {
        return new DefaultOAuth2UserService();
    }
}


