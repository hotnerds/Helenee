package com.hotnerds.unit.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotnerds.unit.ControllerTest;
import com.hotnerds.utils.WithCustomMockUser;
import com.hotnerds.user.domain.dto.*;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.presentation.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends ControllerTest {

    private User followed;

    @BeforeEach
    void init() {
        followed = spy(new User("followedUser", "email"));
        when(followed.getId()).thenReturn(2L);
    }

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
        MvcResult result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(userData.get(0).getUsername()))
                .andExpect(jsonPath("$[1].username").value(userData.get(1).getUsername()))
                .andDo(document("users/getAll"))
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
        MvcResult result = mockMvc.perform(get("/api/users/{user_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andDo(document("users/getOne",
                        pathParameters(
                                parameterWithName("user_id").description("??????ID")
                        ),
                        responseFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("??????????????????")
                        )))
                .andReturn();
    }

    @Test
    @WithCustomMockUser
    void createUser() throws Exception {
        NewUserReqDto newUserReqDto = new NewUserReqDto("PeterLim", "lkslyj2@naver.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUserReqDto)))
                .andExpect(status().isOk())
                .andDo(document("users/create",
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("??????????????????")
                        )));
    }

    @Test
    @WithCustomMockUser
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{user_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("users/delete",
                        pathParameters(
                                parameterWithName("user_id").description("??????ID")
                        )));
    }

    @Test
    @WithCustomMockUser
    void updateUser() throws Exception {
        // given
        UserUpdateReqDto userUpdateReqDto = new UserUpdateReqDto("PeterLim");

        mockMvc.perform(put("/api/users/{user_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateReqDto)))
                .andExpect(status().isOk())
                .andDo(document("users/update",
                        pathParameters(
                                parameterWithName("user_id").description("??????ID")
                        ),
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("????????? ??????")
                        )));
    }

    @DisplayName("???????????? ????????? ????????? ????????? ????????? ?????? ????????? ????????? ??? ??????")
    @Test
    @WithCustomMockUser
    void ?????????_??????() throws Exception {
        // given
        authUser = spy(authUser);
        when(authUser.getId()).thenReturn(1L);

        // when then
        mockMvc.perform(post("/api/users/{followedId}/follow", followed.getId()))
                .andExpect(status().isOk())
                .andDo(document("follow/create",
                        pathParameters(
                                parameterWithName("followedId").description("???????????? ????????? ID")
                        )
                ));
    }

    @DisplayName("???????????? ????????? ????????? ????????? ?????? ????????? ????????? ?????? ????????? ????????? ??? ??????.")
    @Test
    @WithCustomMockUser
    void ?????????_??????() throws Exception {
        // given
        authUser = spy(authUser);
        when(authUser.getId()).thenReturn(1L);

        // when then
        mockMvc.perform(delete("/api/users/{followedId}/follow", followed.getId()))
                .andExpect(status().isNoContent())
                .andDo(document("follow/delete",
                        pathParameters(
                                parameterWithName("followedId").description("followed ????????? ID")
                        )
                ));
    }

    @DisplayName("???????????? ????????? ????????? ?????? ????????? ?????? ????????? ?????? ????????? ????????? ??? ??????")
    @Test
    @WithCustomMockUser
    void ?????????_??????() throws Exception {
        // given
        authUser = spy(authUser);
        when(authUser.getId()).thenReturn(1L);
        FollowServiceReqDto reqDto = new FollowServiceReqDto(authUser.getId(), followed.getId());
        when(userService.followCheck(reqDto)).thenReturn(true);

        // when then
        mockMvc.perform(get("/api/users/{followedId}/follow/check", followed.getId()))
                .andExpect(status().isOk())
                .andDo(document("follow/check",
                        pathParameters(
                                parameterWithName("followedId").description("followed ????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("answer").type(JsonFieldType.BOOLEAN).description("followed ????????? ?????? ????????? ??????")
                        )
                ));
    }

    @DisplayName("?????? ????????? ??????????????? ?????? ????????? id, username, email??? ????????? ??? ??????")
    @Test
    @WithCustomMockUser
    void ??????_?????????_??????() throws Exception {
        // given
        User user1 = spy(new User("user1", "email1"));
        User user2 = spy(new User("user2", "email2"));
        User user3 = spy(new User("user3", "email3"));
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);
        when(user3.getId()).thenReturn(3L);
        List<FollowUserInfoResponseDto> userInfoList = List.of(
                new FollowUserInfoResponseDto(user2.getId(), user2.getUsername(), user2.getEmail()),
                new FollowUserInfoResponseDto(user3.getId(), user3.getUsername(), user3.getEmail())
        );
        when(userService.getUserFollowers(user1.getId())).thenReturn(userInfoList);

        // when then
        mockMvc.perform(get("/api/users/{userId}/follower", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(user2.getUsername()))
                .andExpect(jsonPath("$[1].username").value(user3.getUsername()))
                .andDo(document("follow/get_follower",
                        pathParameters(
                                parameterWithName("userId").description("?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("?????? ID"),
                                fieldWithPath("[].username").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("[].email").type(JsonFieldType.STRING).description("??????????????????")
                        )
                ));
    }

    @DisplayName("?????? ????????? ??????????????? ?????? ????????? id, username, email??? ????????? ??? ??????")
    @Test
    @WithCustomMockUser
    void ??????_????????????_??????() throws Exception {
        // given
        User user1 = spy(new User("user1", "email1"));
        User user2 = spy(new User("user2", "email2"));
        User user3 = spy(new User("user3", "email3"));
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);
        when(user3.getId()).thenReturn(3L);
        List<FollowUserInfoResponseDto> userInfoList = List.of(
                new FollowUserInfoResponseDto(user2.getId(), user2.getUsername(), user2.getEmail()),
                new FollowUserInfoResponseDto(user3.getId(), user3.getUsername(), user3.getEmail())
        );
        when(userService.getUserFollowings(user1.getId())).thenReturn(userInfoList);

        // when then
        mockMvc.perform(get("/api/users/{userId}/followed", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(user2.getUsername()))
                .andExpect(jsonPath("$[1].username").value(user3.getUsername()))
                .andDo(document("follow/get_followed",
                        pathParameters(
                                parameterWithName("userId").description("?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("?????? ID"),
                                fieldWithPath("[].username").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("[].email").type(JsonFieldType.STRING).description("??????????????????")
                        )
                ));
    }

    @DisplayName("?????? ????????? ??????????????? ?????? ????????? ?????? ????????????")
    @Test
    @WithCustomMockUser
    void ??????_?????????_???_??????() throws Exception {
        // given
        Integer INT = 5;
        authUser = spy(authUser);
        when(authUser.getId()).thenReturn(1L);
        when(userService.getFollowerCounts(authUser.getId())).thenReturn(INT);

        // when then
        mockMvc.perform(get("/api/users/{userId}/follower/count", authUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(INT))
                .andDo(document("follow/get_follower_count",
                        pathParameters(
                                parameterWithName("userId").description("?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("????????? ???")
                        )
                ));
    }

    @DisplayName("?????? ????????? ??????????????? ?????? ????????? ?????? ????????????")
    @Test
    @WithCustomMockUser
    void ??????_????????????_???_??????() throws Exception {
        // given
        Integer INT = 5;
        authUser = spy(authUser);
        when(authUser.getId()).thenReturn(1L);
        when(userService.getFollowCounts(authUser.getId())).thenReturn(INT);

        // when then
        mockMvc.perform(get("/api/users/{userId}/followed/count", authUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(INT))
                .andDo(document("follow/get_followed_count",
                        pathParameters(
                                parameterWithName("userId").description("?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("????????? ???")
                        )
                ));
    }
    
}