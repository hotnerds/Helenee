package com.hotnerds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotnerds.common.security.filter.JwtAuthenticationFilter;
import com.hotnerds.common.security.handler.OAuth2AuthenticationEntryPoint;
import com.hotnerds.common.security.handler.OAuth2SuccessHandler;
import com.hotnerds.common.security.oauth2.provider.JwtTokenProvider;
import com.hotnerds.common.security.oauth2.resolver.AuthenticatedUserMethodArgumentResolver;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.common.security.oauth2.service.CustomOAuth2UserService;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ComponentScan(basePackages = "com.hotnerds.common.security")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class ControllerTest {

    public static final String USER_EMAIL = "kgr4163@naver.com";
    public static final String NAME_ATTRIBUTE_KEY = "id";
    public static final String CLIENT_REGISTRATION_ID = "kakao";

    @MockBean
    protected UserRepository userRepository;


    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

        @BeforeEach
        protected void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) throws Exception {
            JwtAuthenticationFilter jwtAuthenticationFilter = (JwtAuthenticationFilter) webApplicationContext.getBean("jwtAuthenticationFilter");

            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    .addFilter(new CharacterEncodingFilter("UTF-8", true))
                    .addFilter(jwtAuthenticationFilter)
                    .apply(springSecurity())
                    .apply(documentationConfiguration(restDocumentation))
                    .alwaysDo(print())
                    .build();

            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
