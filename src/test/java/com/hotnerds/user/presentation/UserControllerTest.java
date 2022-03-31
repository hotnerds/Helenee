package com.hotnerds.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.user.application.UserService;
import com.hotnerds.user.domain.dto.NewUserReqDto;
import com.hotnerds.user.domain.dto.UserUpdateReqDto;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends ControllerTest {

    @MockBean
    private UserService userService;

    @Test
    @WithCustomMockUser
    void getAllUser() throws Exception {
        // given
        List<User> userData = Arrays.asList(
                User.builder()
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
        MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(userData.get(0).getUsername()))
                .andExpect(jsonPath("$[1].username").value(userData.get(1).getUsername()))
                .andDo(document("/users/getAll"))
                .andReturn();
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

        // when then
        MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders.get("/users/{user_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andDo(document("/users/getOne",
                        pathParameters(
                                parameterWithName("user_id").description("유저ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("전자메일주소")
                        )))
                .andReturn();
    }

    @Test
    @WithCustomMockUser
    void createUser() throws Exception {
        NewUserReqDto newUserReqDto = new NewUserReqDto("PeterLim", "lkslyj2@naver.com");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUserReqDto)))
                .andExpect(status().isOk())
                .andDo(document("/users/create",
                        relaxedRequestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("전자메일주소")
                        )));
    }

    @Test
    @WithCustomMockUser
    void deleteUser() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/{user_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("/users/delete",
                        pathParameters(
                                parameterWithName("user_id").description("유저ID")
                        )));
    }

    @Test
    @WithCustomMockUser
    void updateUser() throws Exception {
        // given
        UserUpdateReqDto userUpdateReqDto = new UserUpdateReqDto("PeterLim");

        mockMvc.perform(RestDocumentationRequestBuilders.put("/users/{user_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateReqDto)))
                .andExpect(status().isOk())
                .andDo(document("/users/update",
                        pathParameters(
                                parameterWithName("user_id").description("유저ID")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 이름")
                        )));
    }

}