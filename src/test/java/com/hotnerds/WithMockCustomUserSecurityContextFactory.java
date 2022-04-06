package com.hotnerds;

import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.hotnerds.ControllerTest.NAME_ATTRIBUTE_KEY;
import static com.hotnerds.ControllerTest.USER_EMAIL;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(USER_EMAIL, annotation.getEmail());
        attributes.put(NAME_ATTRIBUTE_KEY, annotation.getNameAttributeKey());

        DefaultOAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(ROLE.USER.getKey())), attributes,
                annotation.getNameAttributeKey());

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), annotation.getClientRegistrationId());

        context.setAuthentication(authentication);

        return context;
    }
}
