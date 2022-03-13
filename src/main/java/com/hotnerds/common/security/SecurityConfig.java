package com.hotnerds.common.security;

import com.hotnerds.common.security.filter.JwtAuthenticationFilter;
import com.hotnerds.common.security.handler.OAuth2SuccessHandler;
import com.hotnerds.common.security.oauth2.provider.CustomOAuth2Provider;
import com.hotnerds.common.security.oauth2.provider.JwtTokenProvider;
import com.hotnerds.common.security.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuthSuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/", "/oauth/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login().loginPage("/oauth2/authorization/kakao")
                        .userInfoEndpoint()
                            .userService(customOAuth2UserService)
                .and()
                    .successHandler(oAuthSuccessHandler)
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), OAuth2LoginAuthenticationFilter.class);

    }



}