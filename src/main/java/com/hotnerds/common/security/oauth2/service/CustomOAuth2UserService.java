package com.hotnerds.common.security.oauth2.service;

import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.user.domain.dto.UserUpdateReqDto;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final DefaultOAuth2UserService defaultOAuth2UserService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo attributes = OAuth2UserInfo.of(registrationId, oAuth2User.getAttributes());

        User user = saveOrUpdateUser(attributes, registrationId);

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                oAuth2User.getName());
    }

    private User saveOrUpdateUser(OAuth2UserInfo attributes, String registrationId) {

        Optional<User> optionalUser = userRepository.findByEmail(attributes.getEmail())
                .map(user -> user.updateUser(UserUpdateReqDto.builder()
                        .username(attributes.getName())
                        .build()));

        AuthProvider authProvider = Arrays.stream(AuthProvider.values())
                .filter(provider -> provider.getRegistrationId().equals(registrationId))
                .findAny()
                .orElseThrow(() -> new OAuth2AuthenticationException(ErrorCode.AUTHENTICATION_PROVIDER_NOT_FOUND.getMessage()));

        return optionalUser.orElseGet(() -> userRepository.save(new User(attributes.getName(), attributes.getEmail(), ROLE.USER, authProvider)));

    }
}