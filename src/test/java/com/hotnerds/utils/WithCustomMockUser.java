package com.hotnerds.utils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hotnerds.unit.ControllerTest.*;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

    String getEmail() default USER_EMAIL;

    String getClientRegistrationId() default CLIENT_REGISTRATION_ID;

    String getNameAttributeKey() default NAME_ATTRIBUTE_KEY;
}
