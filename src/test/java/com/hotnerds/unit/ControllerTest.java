package com.hotnerds.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotnerds.comment.application.CommentService;
import com.hotnerds.common.security.filter.JwtAuthenticationFilter;
import com.hotnerds.common.security.oauth2.provider.JwtTokenProvider;
import com.hotnerds.common.security.oauth2.service.AuthProvider;
import com.hotnerds.diet.application.DietService;
import com.hotnerds.food.application.FoodService;
import com.hotnerds.post.application.PostService;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.ROLE;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ComponentScan(basePackages = "com.hotnerds.common.security")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class ControllerTest {

    public static final String USER_EMAIL = "kgr4163@naver.com";
    public static final String NAME_ATTRIBUTE_KEY = "id";
    public static final String CLIENT_REGISTRATION_ID = "kakao";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_TOKEN = "access token";

    @MockBean
    protected UserRepository userRepository;

    protected User authUser;
    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    @MockBean
    protected PostService postService;

    @MockBean
    protected FoodService foodService;

    @MockBean
    protected DietService dietService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected CommentService commentService;

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

        authUser = new User("garamkim", "kgr4163@naver.com", ROLE.USER, AuthProvider.KAKAO);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(authUser));
        when(jwtTokenProvider.resolveToken(any())).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.validateToken(any())).thenReturn(false);
    }
}
