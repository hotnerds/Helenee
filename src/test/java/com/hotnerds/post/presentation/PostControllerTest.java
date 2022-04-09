package com.hotnerds.post.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.post.application.PostService;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.dto.PostResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PostController.class})
class PostControllerTest extends ControllerTest {

    @MockBean
    PostService postService;

    List<PostResponseDto> postResponse;

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
    }

    @WithCustomMockUser
    @DisplayName("사용자는 전체 게시물을 조회할 수 있다.")
    @Test
    void 전체_게시물_조회_요청() throws Exception{
        //given
        when(postService.searchAll(any())).thenReturn(postResponse);

        //when
        mockMvc.perform(get("/api/posts?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithCustomMockUser
    @DisplayName("사용자는 게시글을 이름으로 조회할 수 있다.")
    @Test
    void 게시글_이름으로_조회() throws Exception{
        //given
        when(postService.searchByTitle(any())).thenReturn(postResponse);

        //when
        mockMvc.perform(get("/api/posts?page=0&size=10&title=title")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}