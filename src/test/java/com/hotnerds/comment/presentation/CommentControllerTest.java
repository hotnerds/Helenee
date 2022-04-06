package com.hotnerds.comment.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.dto.*;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private CommentService commentService;

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

    @DisplayName("GET /api/comment 요청을 보내면 특정 게시글의 댓글을 페이징해서 반환한다")
    @Test
    @WithCustomMockUser
    void 댓글_조회_API() throws Exception {
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
                                parameterWithName("postId").description("게시글ID")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("페이지의 크기")
                        ),
                        responseFields(
                                fieldWithPath("[].commentId").type(JsonFieldType.NUMBER).description("새로 생성된 댓글ID"),
                                fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("작성자ID"),
                                fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("게시글ID"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("생성 시간")
                        )
                ))
                .andReturn();
        verify(commentService, times(1)).getComments(any(CommentByPostReqDto.class));
    }

    @DisplayName("POST /api/posts/{post_id}/comments 요청을 보내면 DB에 새로운 댓글이 생성된다")
    @Test
    @WithCustomMockUser
    void 댓글_생성_API() throws Exception {
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
                                parameterWithName("postId").description("게시글ID")
                        ),
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저ID"),
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("조회된 댓글ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("작성자ID"),
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간")
                        )
                ));
        verify(commentService, times(1)).addComment(any(CommentCreateReqDto.class));
    }

    @DisplayName("DELETE /api/posts/{post_id}/comments/{comment_id} 요청을 보내면 post_id, comment_id에 해당하는 댓글이 삭제된다")
    @Test
    @WithCustomMockUser
    void 댓글_삭제_API() throws Exception {
        // given

        // when then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L))
                .andExpect(status().isNoContent())
                .andDo(document("comments/delete",
                        pathParameters(
                                parameterWithName("postId").description("게시글ID"),
                                parameterWithName("commentId").description("댓글ID")
                        )
                ));
        verify(commentService, times(1)).deleteComment(any(CommentDeleteReqDto.class), any());
    }

    @DisplayName("PATCH /api/posts/{post_id}/comments/{comment_id} 요청을 보내면 해당 comment_id를 가진 댓글의 내용이 수정된다.")
    @Test
    @WithCustomMockUser
    void 댓글_수정_API() throws Exception {
        // given
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(1L)
                .commentId(1L)
                .content(NEW_TEXT)
                .build();
        when(commentService.updateComment(any(CommentUpdateReqDto.class), any())).thenReturn(comment); // userId에 anyLong을 주면 user.getId()에서 예외가 발생해서 any()로 넣음

        // when then
        mockMvc.perform(patch("/api/posts/{postId}/comments/{commentId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()))
                .andDo(document("comments/update",
                        pathParameters(
                                parameterWithName("postId").description("게시글ID"),
                                parameterWithName("commentId").description("댓글ID")
                        ),
                        requestFields(
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글ID"),
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("게시글ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("수정 후 댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("조회된 댓글ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("작성자ID"),
                                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글ID"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("수정된 내용"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("생성 시간")
                        )
                ));
        verify(commentService, times(1)).updateComment(any(CommentUpdateReqDto.class), any());
    }

}