package com.hotnerds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotnerds.common.security.filter.JwtAuthenticationFilter;
import com.hotnerds.common.security.handler.OAuth2SuccessHandler;
import com.hotnerds.common.security.oauth2.provider.JwtTokenProvider;
import com.hotnerds.common.security.oauth2.resolver.AuthenticatedUserMethodArgumentResolver;
import com.hotnerds.common.security.oauth2.service.AuthenticatedUser;
import com.hotnerds.common.security.oauth2.service.CustomOAuth2UserService;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

//@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    public static final String USER_EMAIL = "kgr4163@naver.com";
    public static final String NAME_ATTRIBUTE_KEY = "id";
    public static final String CLIENT_REGISTRATION_ID = "kakao";

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    AuthenticatedUserMethodArgumentResolver resolver;

    @MockBean
    CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    OAuth2SuccessHandler oAuth2SuccessHandler;

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp(WebApplicationContext webApplicationContext) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = (JwtAuthenticationFilter) webApplicationContext.getBean("jwtAuthenticationFilter");

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .addFilter(jwtAuthenticationFilter)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        User user = User.builder()
                .email(USER_EMAIL)
                .username("garamkim")
                .build();

        when(resolver.supportsParameter(any())).thenReturn(true);
        when(resolver.resolveArgument(any(), any(), any(), any())).thenReturn(AuthenticatedUser.of(user));
    }
}
