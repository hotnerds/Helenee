package com.hotnerds.unit.post.presentation;

import com.hotnerds.unit.ControllerTest;
import com.hotnerds.utils.WithCustomMockUser;
import com.hotnerds.post.domain.dto.LikeResponseDto;
import com.hotnerds.post.domain.dto.PostRequestDto;
import com.hotnerds.post.domain.dto.PostResponseDto;
import com.hotnerds.post.domain.dto.PostUpdateRequestDto;
import com.hotnerds.post.presentation.PostController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;

import static com.hotnerds.utils.DocumentUtils.getDocumentRequestPreprocess;
import static com.hotnerds.utils.DocumentUtils.getDocumentResponsePreprocess;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PostController.class})
class PostControllerTest extends ControllerTest {

    PostResponseDto postResponse;

    MultiValueMap<String, String> params;

    @BeforeEach
    void init() {
        postResponse = PostResponseDto.builder()
                .postId(1L)
                .title("title")
                .writer("garamkim")
                .content("content")
                .createdAt(LocalDateTime.now())
                .likeCount(1)
                .tagNames(List.of("tag"))
                .build();

        params = new LinkedMultiValueMap<>();
        params.put("page", List.of("0"));
        params.put("size", List.of("10"));
    }

    @WithCustomMockUser
    @DisplayName("???????????? ???????????? ????????? ??? ??????.")
    @Test
    void ?????????_??????_??????() throws Exception {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title("title")
                .content("content")
                .tagNames(List.of("tag"))
                .build();
        when(postService.write(any(), any())).thenReturn(1L);

        //when then
        ResultActions resultActions = mockMvc.perform(post("/api/posts")
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        resultActions.andExpect(status().isCreated())
                .andDo(
                        document("posts/post-create",
                            getDocumentRequestPreprocess(),
                            getDocumentResponsePreprocess(),
                            requestHeaders(
                                    headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                            ),
                            requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                    fieldWithPath("tagNames").type(JsonFieldType.ARRAY).description("????????? ?????? ??????")
                            ),
                            responseHeaders(
                                    headerWithName("Location").description("????????? ????????? ID??? ?????? URI")
                            )));
    }

    @WithCustomMockUser
    @DisplayName("???????????? ?????? ???????????? ????????? ??? ??????.")
    @Test
    void ??????_?????????_??????_??????() throws Exception{
        //given
        when(postService.searchAll(any())).thenReturn(List.of(postResponse));

        //when then
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN)
                .params(params));

        resultActions
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "posts/post-get-all",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("page").optional().description("????????? ????????? ??????"),
                                        parameterWithName("size").optional().description("????????? ???????????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("???????????? ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("????????? ?????? ??????")
                                )));

    }

    @WithCustomMockUser
    @DisplayName("???????????? ???????????? ????????? ID??? ????????? ??? ??????.")
    @Test
    void ?????????_ID???_??????() throws Exception {
        //given
        when(postService.searchByPostId(any())).thenReturn(postResponse);

        //when then
        ResultActions resultActions = mockMvc.perform(get("/api/posts/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN));

        resultActions.andExpect(status().isOk())
                .andDo(
                        document(
                                "posts/post-get-by-id",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("????????? ????????? ID")
                                ),
                                responseFields(
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("???????????? ID"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("writer").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                        fieldWithPath("tagNames").type(JsonFieldType.ARRAY).description("????????? ?????? ??????")
                                )));

    }

    @WithCustomMockUser
    @DisplayName("???????????? ???????????? ???????????? ????????? ??? ??????.")
    @Test
    void ?????????_????????????_??????() throws Exception{
        //given
        when(postService.searchByTitle(any())).thenReturn(List.of(postResponse));
        params.put("title", List.of("title"));
        //when then
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN)
                        .params(params));

        resultActions
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "posts/post-get-by-title",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("title").description("?????? ????????? ??????"),
                                        parameterWithName("page").optional().description("????????? ????????? ??????"),
                                        parameterWithName("size").optional().description("????????? ???????????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("???????????? ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("????????? ?????? ??????")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("????????? ???????????? ???????????? ????????? ??? ??????.")
    @Test
    void ?????????_?????????_????????????_??????() throws Exception {
        //given
        when(postService.searchByWriter(any())).thenReturn(List.of(postResponse));
        params.put("writer", List.of("garamkim"));

        //when then
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN)
                .params(params));

        resultActions
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "posts/post-get-by-writer",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("writer").description("?????? ????????? ????????? ??????"),
                                        parameterWithName("page").optional().description("????????? ????????? ??????"),
                                        parameterWithName("size").optional().description("????????? ???????????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("???????????? ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("????????? ?????? ??????")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("???????????? ?????? tag ???????????? ????????? ????????? ??? ??????.")
    @Test
    void ??????_????????????_?????????_??????() throws Exception {
        //given
        when(postService.searchByTagNames(any())).thenReturn(List.of(postResponse));
        params.put("tagNames", List.of("tag"));

        //when then
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN)
                .params(params));

        resultActions
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "posts/post-get-by-tagNames",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("tagNames").description("?????? ???????????? ?????? ??????"),
                                        parameterWithName("page").optional().description("????????? ????????? ??????"),
                                        parameterWithName("size").optional().description("????????? ???????????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("???????????? ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("???????????? ??????"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("????????? ?????????"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("????????? ?????? ??????")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("????????? ???????????? ????????? ?????? ????????? ??? ??????.")
    @Test
    void ?????????_??????_??????() throws Exception {
        //given
        doNothing().when(postService).delete(any(), any());

        //when then
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/{id}", 1L)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN));

        resultActions.andExpect(status().isNoContent())
                .andDo(
                        document(
                                "posts/post-delete",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("????????? ????????? ID")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("????????? ???????????? ???????????? ????????? ??? ??????.")
    @Test
    void ?????????_??????_??????() throws Exception {
        //given
        doNothing().when(postService).update(any(), any());
        PostUpdateRequestDto requestDto = PostUpdateRequestDto.builder()
                .postId(1L)
                .title("title")
                .content("content")
                .tagNames(List.of())
                .build();

        //when then
        ResultActions resultActions = mockMvc.perform(patch("/api/posts/{id}", 1L)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        resultActions.andExpect(status().isNoContent())
                .andDo(
                        document(
                                "posts/post-update",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("????????? ???????????? ID")
                                ),
                                requestFields(
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("????????? ????????? ID"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("tagNames").type(JsonFieldType.ARRAY).optional().description("????????? ????????? ?????? ??????")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("???????????? ???????????? ???????????? ?????? ??? ??????")
    @Test
    void ?????????_??????() throws Exception {
        //given
        LikeResponseDto responseDto = LikeResponseDto.builder()
                .postId(1L)
                .writer("garamkim")
                .likeCount(1)
                .build();
        when(postService.like(any(), any())).thenReturn(responseDto);

        //when then
        ResultActions resultActions = mockMvc.perform(post("/api/posts/{id}/likes", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN));

        resultActions.andExpect(status().isOk())
                .andDo(
                        document(
                                "posts/post-likes",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("????????? ??? ????????? ID")
                                ),
                                responseFields(
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                        fieldWithPath("writer").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("????????? ID")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("???????????? ???????????? ?????? ???????????? ???????????? ????????? ??? ??????.")
    @Test
    void ?????????_??????_??????() throws Exception {
        //given
        LikeResponseDto responseDto = LikeResponseDto.builder()
                .postId(1L)
                .writer("garamkim")
                .likeCount(0)
                .build();
        when(postService.unlike(any(), any())).thenReturn(responseDto);

        //when then
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/{id}/likes", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN));

        resultActions.andExpect(status().isOk())
                .andDo(
                        document(
                                "posts/post-unlikes",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("????????? Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("???????????? ????????? ????????? ID")
                                ),
                                responseFields(
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                        fieldWithPath("writer").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("????????? ID")
                                )));
    }

}