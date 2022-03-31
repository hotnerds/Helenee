package com.hotnerds.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.dto.NewUserReqDto;
import com.hotnerds.user.domain.dto.UserUpdateReqDto;
import com.hotnerds.user.domain.User;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends ControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithCustomMockUser
    void getAllUser() throws Exception {
        // given
        List<User> userData = Arrays.asList(User.builder()
                .username("RetepMil")
                .email("lkslyj2@naver.com")
                .build(),
                User.builder()
                        .username("PeterLim")
                        .email("lkslyj8@naver.com")
                        .build()
        );


        when(userService.getAllUsers()).thenReturn(userData);
        when(userRepository.findAll()).thenReturn(userData);

        // mocking
        // when
        MvcResult result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
        DocumentContext documentContext = JsonPath.parse(result.getResponse().getContentAsString());

        //then
        Assertions.assertEquals(documentContext.read("$[*]['username']").toString(), "[\"RetepMil\",\"PeterLim\"]");
        Assertions.assertEquals(documentContext.read("$[*]['email']").toString(), "[\"lkslyj2@naver.com\",\"lkslyj8@naver.com\"]");

    }

    @Test
    @WithCustomMockUser
    void createUser() throws Exception {
        NewUserReqDto newUserReqDto = new NewUserReqDto("PeterLim", "lkslyj2@naver.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUserReqDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void getUser() throws Exception {
        // given
        User user = User.builder()
                .username("RetepMil")
                .email("lkslyj2@naver.com")
                .build();

        // mocking
        when(userService.getUserById(1L)).thenReturn(user);

        // when
        MvcResult result = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();
        DocumentContext documentContext = JsonPath.parse(result.getResponse().getContentAsString());

        // then
        Assertions.assertEquals((String)documentContext.read("username"), "RetepMil");
        Assertions.assertEquals((String)documentContext.read("email"), "lkslyj2@naver.com");
    }

    @Test
    @WithCustomMockUser
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithCustomMockUser
    void updateUser() throws Exception {
        // given
        UserUpdateReqDto userUpdateReqDto = new UserUpdateReqDto("PeterLim");

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateReqDto)))
                .andExpect(status().isOk());
    }

}