package com.hotnerds.post.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.post.application.PostService;
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
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
    void 태그_이름들로_게시글_조회() throws Exception{
        //given
        when(postService.searchByTagNames(any())).thenReturn(postResponse);
        params.put("tagNames", List.of("tag"));

        //when then
        mockMvc.perform(get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON)
                        .params(params))
                        .andExpect(status().isOk());
    }
}