package com.hotnerds.unit.comment.presentation;

import com.hotnerds.unit.ControllerTest;
import com.hotnerds.utils.WithCustomMockUser;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.dto.*;
import com.hotnerds.comment.presentation.CommentController;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest extends ControllerTest {

    final static String TEXT = "An apple a day keeps the doctor away";
    final static String NEW_TEXT = TEXT + TEXT;
    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = Mockito.spy(new User("user", "email", ROLE.USER));
        when(user.getId()).thenReturn(1L);
        post = Mockito.spy(new Post("title", TEXT, user));
        when(post.getId()).thenReturn(1L);
        comment = Mockito.spy(new Comment(1L, user, post, TEXT));
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.MIN);
    }

    @DisplayName("GET /api/comment ????????? ????????? ?????? ???????????? ????????? ??????????????? ????????????")
    @Test
    @WithCustomMockUser
    void ??????_??????_API() throws Exception {
        // given
        Comment comment2 = Mockito.spy(new Comment(2L, user, post, NEW_TEXT));
        when(comment2.getCreatedAt()).thenReturn(LocalDateTime.MAX);
        int page = 0;
        int size = 10;

        List<CommentResponseDto> response = List.of(
                CommentResponseDto.of(comment),
                CommentResponseDto.of(comment2)
        );
        when(commentService.getComments(any(CommentByPostReqDto.class))).thenReturn(response);

        // when
        MvcResult result = mockMvc.perform(get("/api/posts/{postId}/comments?&page=" + page +"&size=" + size, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(TEXT))
                .andExpect(jsonPath("$[1].content").value(NEW_TEXT))
                .andDo(document("comments/get",
                        pathParameters(
                                parameterWithName("postId").description("?????????ID")
                        ),
                        requestParameters(
                                parameterWithName("page").description("?????????"),
                                parameterWithName("size").description("???????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("?????? ????????? ??????ID"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("?????? ??????")
                        )
                ))
                .andReturn();
        verify(commentService, times(1)).getComments(any(CommentByPostReqDto.class));
    }

    @DisplayName("POST /api/posts/{post_id}/comments ????????? ????????? DB??? ????????? ????????? ????????????")
    @Test
    @WithCustomMockUser
    void ??????_??????_API() throws Exception {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(user.getId())
                .postId(post.getId())
                .content(TEXT)
                .build();

        when(commentService.addComment(any(CommentCreateReqDto.class))).thenReturn(comment);

        // when then
        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()))
                .andDo(document("comments/create",
                        pathParameters(
                                parameterWithName("postId").description("?????????ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("??????ID"),
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("????????? ??????ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ??????")
                        )
                ));
        verify(commentService, times(1)).addComment(any(CommentCreateReqDto.class));
    }

    @DisplayName("DELETE /api/posts/{post_id}/comments/{comment_id} ????????? ????????? post_id, comment_id??? ???????????? ????????? ????????????")
    @Test
    @WithCustomMockUser
    void ??????_??????_API() throws Exception {
        // given

        // when then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L))
                .andExpect(status().isNoContent())
                .andDo(document("comments/delete",
                        pathParameters(
                                parameterWithName("postId").description("?????????ID"),
                                parameterWithName("commentId").description("??????ID")
                        )
                ));
        verify(commentService, times(1)).deleteComment(any(CommentDeleteReqDto.class), any());
    }

    @DisplayName("PATCH /api/posts/{post_id}/comments/{comment_id} ????????? ????????? ?????? comment_id??? ?????? ????????? ????????? ????????????.")
    @Test
    @WithCustomMockUser
    void ??????_??????_API() throws Exception {
        // given
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(1L)
                .commentId(1L)
                .content(NEW_TEXT)
                .build();
        when(commentService.updateComment(any(CommentUpdateReqDto.class), any())).thenReturn(comment); // userId??? anyLong??? ?????? user.getId()?????? ????????? ???????????? any()??? ??????

        // when then
        mockMvc.perform(patch("/api/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()))
                .andDo(document("comments/update",
                        pathParameters(
                                parameterWithName("postId").description("?????????ID"),
                                parameterWithName("commentId").description("??????ID")
                        ),
                        requestFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("????????? ??????ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("?????????ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ??????")
                        )
                ));
        verify(commentService, times(1)).updateComment(any(CommentUpdateReqDto.class), any());
    }

}