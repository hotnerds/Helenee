package com.hotnerds.comment.presentation;

import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.dto.*;
import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest extends ControllerTest {

    @MockBean
    private CommentService commentService;

    final static String TEXT = "An apple a day keeps the doctor away";

    @DisplayName("POST /api/posts/{post_id}/comments 요청을 보내면 DB에 새로운 댓글이 생성된다")
    @Test
    @WithCustomMockUser
    void 댓글_생성_API() throws Exception {
        // given
        User user = new User("user", "email", ROLE.USER);
        Post post = new Post("title", TEXT, user);
        Comment comment = new Comment(1L, user, post, TEXT);

        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(user.getId())
                .postId(post.getId())
                .content(TEXT)
                .build();

        when(commentService.addComment(any(CommentCreateReqDto.class))).thenReturn(comment);

        // when then
        mockMvc.perform(post("/api/posts/1/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()));
        verify(commentService, times(1)).addComment(any(CommentCreateReqDto.class));
    }

    @DisplayName("DELETE /api/posts/{post_id}/comments/{comment_id} 요청을 보내면 post_id, comment_id에 해당하는 댓글이 삭제된다")
    @Test
    @WithCustomMockUser
    void 댓글_삭제_API() throws Exception {
        // given

        // when then
        mockMvc.perform(delete("/api/posts/1/comments/1"))
                .andExpect(status().isNoContent());
        verify(commentService, times(1)).deleteComment(any(CommentDeleteReqDto.class), any());
    }

    @DisplayName("PATCH /api/posts/{post_id}/comments/{comment_id} 요청을 보내면 해당 comment_id를 가진 댓글의 내용이 수정된다.")
    @Test
    @WithCustomMockUser
    void 댓글_수정_API() throws Exception {
        // given
        String NEW_TEXT = TEXT + TEXT;
        User user = new User("user", "email", ROLE.USER);
        Post post = new Post("title", TEXT, user);
        Comment comment = new Comment(1L, user, post, NEW_TEXT);
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(1L)
                .commentId(1L)
                .content(NEW_TEXT)
                .build();
        when(commentService.updateComment(any(CommentUpdateReqDto.class), any())).thenReturn(comment); // userId에 anyLong을 주면 user.getId()에서 예외가 발생해서 any()로 넣음

        // when then
        mockMvc.perform(patch("/api/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()));
        verify(commentService, times(1)).updateComment(any(CommentUpdateReqDto.class), any());
    }

    @DisplayName("GET /api/comment 요청을 보내면 특정 게시글의 댓글을 페이징해서 반환한다")
    @Test
    @WithCustomMockUser
    void 댓글_조회_API() throws Exception {
        // given
        String NEW_TEXT = TEXT + TEXT;
        User user = new User("user", "email", ROLE.USER);
        Post post = new Post("title", TEXT, user);
        Comment comment = new Comment(1L, user, post, TEXT);
        Comment comment2 = new Comment(1L, user, post, NEW_TEXT);

        int page = 0;
        int size = 10;

        List<CommentResponseDto> response = List.of(
                CommentResponseDto.of(comment),
                CommentResponseDto.of(comment2)
        );
        when(commentService.getComments(any(CommentByPostReqDto.class))).thenReturn(response);

        // when
        MvcResult result = mockMvc.perform(get("/api/posts/1/comments?&page=" + page +"&size=" + size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(TEXT))
                .andExpect(jsonPath("$[1].content").value(NEW_TEXT))
                .andReturn();
        verify(commentService, times(1)).getComments(any(CommentByPostReqDto.class));
    }

}