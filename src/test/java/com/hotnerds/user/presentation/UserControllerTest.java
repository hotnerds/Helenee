package com.hotnerds.user.presentation;

import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        User user1 = User.builder().username("retepmil").email("lks@gmail.com").build();
        User user2 = User.builder().username("peterlim").email("lyj@naver.com").build();

        userService.save(user1);
        userRepository.save(user2);
    }

/*    @Test
    void 전체유저조회_테스트() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 아이디로특정유저조회_테스트() {
    }*/

}