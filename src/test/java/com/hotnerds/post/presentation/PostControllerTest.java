package com.hotnerds.post.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.post.application.PostService;
import com.hotnerds.post.domain.dto.LikeResponseDto;
import com.hotnerds.post.domain.dto.PostRequestDto;
import com.hotnerds.post.domain.dto.PostResponseDto;
import com.hotnerds.post.domain.dto.PostUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    PostService postService;

    List<PostResponseDto> postResponse;

    MultiValueMap<String, String> params;

    @BeforeEach
    void init() {
        postResponse = List.of(
            PostResponseDto.builder()
                    .postId(1L)
                    .title("title")
                    .writer("garamkim")
                    .content("content")
                    .createdAt(LocalDateTime.now())
                    .likeCount(1)
                    .tagNames(List.of("tag"))
                    .build());

        params = new LinkedMultiValueMap<>();
        params.put("page", List.of("0"));
        params.put("size", List.of("10"));
    }

    @WithCustomMockUser
    @DisplayName("사용자는 게시물을 작성할 수 있다.")
    @Test
    void 게시물_작성_요청() throws Exception {
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
                        document("post-create",
                            getDocumentRequestPreprocess(),
                            getDocumentResponsePreprocess(),
                            requestHeaders(
                                    headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                            ),
                            requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("게시글의 제목"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("게시글의 본문"),
                                    fieldWithPath("tagNames").type(JsonFieldType.ARRAY).description("게시글 태그 목록")
                            ),
                            responseHeaders(
                                    headerWithName("Location").description("생성한 게시글 ID가 담긴 URI")
                            )));
    }

    @WithCustomMockUser
    @DisplayName("사용자는 전체 게시물을 조회할 수 있다.")
    @Test
    void 전체_게시물_조회_요청() throws Exception{
        //given
        when(postService.searchAll(any())).thenReturn(postResponse);

        //when then
        ResultActions resultActions = mockMvc.perform(get("/api/posts")
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN)
                .params(params));

        resultActions
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "post-get-all",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("page").optional().description("요청할 페이지 위치"),
                                        parameterWithName("size").optional().description("요청할 페이지의 크기")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("게시글의 ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글의 제목"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글의 본문"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("게시글 작성자 이름"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("게시글 작성 날짜"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("게시글 좋아요 개수"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("게시글 태그 목록")
                                )));

    }

    @WithCustomMockUser
    @DisplayName("사용자는 게시글을 이름으로 조회할 수 있다.")
    @Test
    void 게시글_이름으로_조회() throws Exception{
        //given
        when(postService.searchByTitle(any())).thenReturn(postResponse);
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
                                "post-get-by-title",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("title").description("찾을 게시글 제목"),
                                        parameterWithName("page").optional().description("요청할 페이지 위치"),
                                        parameterWithName("size").optional().description("요청할 페이지의 크기")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("게시글의 ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글의 제목"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글의 본문"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("게시글 작성자 이름"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("게시글 작성 날짜"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("게시글 좋아요 개수"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("게시글 태그 목록")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("작성자 이름으로 게시글을 조회할 수 있다.")
    @Test
    void 게시글_작성자_이름으로_조회() throws Exception {
        //given
        when(postService.searchByWriter(any())).thenReturn(postResponse);
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
                                "post-get-by-writer",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("writer").description("찾을 게시글 작성자 이름"),
                                        parameterWithName("page").optional().description("요청할 페이지 위치"),
                                        parameterWithName("size").optional().description("요청할 페이지의 크기")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("게시글의 ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글의 제목"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글의 본문"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("게시글 작성자 이름"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("게시글 작성 날짜"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("게시글 좋아요 개수"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("게시글 태그 목록")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("게시글에 붙은 tag 이름들로 게시글 조회할 수 있다.")
    @Test
    void 태그_이름들로_게시글_조회() throws Exception {
        //given
        when(postService.searchByTagNames(any())).thenReturn(postResponse);
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
                                "post-get-all",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                requestParameters(
                                        parameterWithName("tagNames").description("찾을 게시글의 태그 목록"),
                                        parameterWithName("page").optional().description("요청할 페이지 위치"),
                                        parameterWithName("size").optional().description("요청할 페이지의 크기")
                                ),
                                responseFields(
                                        fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("게시글의 ID"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글의 제목"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글의 본문"),
                                        fieldWithPath("[].writer").type(JsonFieldType.STRING).description("게시글 작성자"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("게시글 작성 날짜"),
                                        fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("게시글 좋아요 개수"),
                                        fieldWithPath("[].tagNames").type(JsonFieldType.ARRAY).description("게시글 태그 목록")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("게시글 작성자는 게시글 삭제 요청할 수 있다.")
    @Test
    void 게시글_삭제_요청() throws Exception {
        //given
        doNothing().when(postService).delete(any(), any());

        //when then
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/{id}", 1L)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN));

        resultActions.andExpect(status().isNoContent())
                .andDo(
                        document(
                                "post-delete",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("삭제할 게시글 ID")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("게시글 작성자는 게시글을 수정할 수 있다.")
    @Test
    void 게시글_수정_요청() throws Exception {
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
                                "post-update",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("수정할 게시글의 ID")
                                ),
                                requestFields(
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("수정할 게시글 ID"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("수정할 게시글 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 게시글 본문"),
                                        fieldWithPath("tagNames").type(JsonFieldType.ARRAY).optional().description("수정할 게시글 태그 목록")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("사용자는 게시글에 좋아요를 누를 수 있다")
    @Test
    void 좋아요_요청() throws Exception {
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
                                "post-likes",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("좋아요 할 게시글 ID")
                                ),
                                responseFields(
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("게시글 좋아요 개수"),
                                        fieldWithPath("writer").type(JsonFieldType.STRING).description("게시글 작성자 이름"),
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID")
                                )));
    }

    @WithCustomMockUser
    @DisplayName("게시글에 좋아요를 누른 사용자는 좋아요를 취소할 수 있다.")
    @Test
    void 좋아요_요청_취소() throws Exception {
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
                                "post-unlikes",
                                getDocumentRequestPreprocess(),
                                getDocumentResponsePreprocess(),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION_HEADER).description("유저의 Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("좋아요를 취소할 게시글 ID")
                                ),
                                responseFields(
                                        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("게시글 좋아요 개수"),
                                        fieldWithPath("writer").type(JsonFieldType.STRING).description("게시글 작성자 이름"),
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID")
                                )));
    }

}