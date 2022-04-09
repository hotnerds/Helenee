package com.hotnerds.post.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.post.application.PostService;
import com.hotnerds.post.domain.dto.LikeResponseDto;
import com.hotnerds.post.domain.dto.PostRequestDto;
import com.hotnerds.post.domain.dto.PostResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
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
                    .title("title")
                    .username("garamkim")
                    .content("content")
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
        mockMvc.perform(post("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                        .andExpect(status().isCreated());
    }

    @WithCustomMockUser
    @DisplayName("사용자는 전체 게시물을 조회할 수 있다.")
    @Test
    void 전체_게시물_조회_요청() throws Exception{
        //given
        when(postService.searchAll(any())).thenReturn(postResponse);

        //when then
        mockMvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .params(params))
                        .andExpect(status().isOk());
    }

    @WithCustomMockUser
    @DisplayName("사용자는 게시글을 이름으로 조회할 수 있다.")
    @Test
    void 게시글_이름으로_조회() throws Exception{
        //given
        when(postService.searchByTitle(any())).thenReturn(postResponse);
        params.put("title", List.of("title"));
        //when then
        mockMvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .params(params))
                        .andExpect(status().isOk());
    }

    @WithCustomMockUser
    @DisplayName("작성자 이름으로 게시글을 조회할 수 있다.")
    @Test
    void 게시글_작성자_이름으로_조회() throws Exception {
        //given
        when(postService.searchByWriter(any())).thenReturn(postResponse);
        params.put("writer", List.of("garamkim"));

        //when then
        mockMvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .params(params))
                        .andExpect(status().isOk());
    }

    @WithCustomMockUser
    @DisplayName("게시글에 붙은 tag 이름들로 게시글 조회할 수 있다.")
    @Test
    void 태그_이름들로_게시글_조회() throws Exception {
        //given
        when(postService.searchByTagNames(any())).thenReturn(postResponse);
        params.put("tagNames", List.of("tag"));

        //when then
        mockMvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .params(params))
                        .andExpect(status().isOk());
    }

    @WithCustomMockUser
    @DisplayName("게시글 작성자는 게시글 삭제 요청할 수 있다.")
    @Test
    void 게시글_삭제_요청() throws Exception {
        //given
        doNothing().when(postService).delete(any(), any());

        //when then
        mockMvc.perform(delete("/api/posts/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @WithCustomMockUser
    @DisplayName("게시글 작성자는 게시글을 수정할 수 있다.")
    @Test
    void 게시글_수정_요청() throws Exception {
        //given
        doNothing().when(postService).update(any(), any());

        //when then
        mockMvc.perform(patch("/api/posts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithCustomMockUser
    @DisplayName("사용자는 게시글에 좋아요를 누를 수 있다")
    @Test
    void 좋아요_요청() throws Exception {
        //given
        LikeResponseDto responseDto = LikeResponseDto.builder()
                .postId(1L)
                .username("garamkim")
                .likeCount(1)
                .build();
        when(postService.like(any(), any())).thenReturn(responseDto);

        //when then
        mockMvc.perform(post("/api/posts/{id}/likes", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

}