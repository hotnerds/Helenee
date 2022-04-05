package com.hotnerds.common.security;

import com.hotnerds.common.security.filter.JwtAuthenticationFilter;
import com.hotnerds.common.security.handler.OAuth2AuthenticationEntryPoint;
import com.hotnerds.common.security.handler.OAuth2SuccessHandler;
import com.hotnerds.common.security.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuthSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationEntryPoint authenticationEntryPoint;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
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
                    .addFilterBefore(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class);

    }



}