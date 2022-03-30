package com.hotnerds.comment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotnerds.ControllerTest;
import com.hotnerds.WithCustomMockUser;
import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.Dto.*;
import com.hotnerds.comment.repository.CommentRepository;
import com.hotnerds.post.domain.Post;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.ROLE;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest extends ControllerTest {

    @MockBean
    private CommentService commentService;

    final static String TEXT = "An apple a day keeps the doctor away";

    @DisplayName("POST /api/comment/ 요청을 보내면 DB에 새로운 댓글이 생성된다")
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
        mockMvc.perform(post("/comment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("DELETE /api/comment/{comment_id} 요청을 보내면 Dto에 있는 정보에 해당하는 댓글이 삭제된다")
    @Test
    @WithCustomMockUser
    void 댓글_삭제_API() throws Exception {
        // given
        LocalDateTime testLocalDateTime = LocalDateTime.now();
        CommentDeleteReqDto reqDto = CommentDeleteReqDto.builder()
                .postId(1L)
                .commentId(1L)
                .createAt(testLocalDateTime)
                .build();

        // when then
        mockMvc.perform(delete("/comment/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isAccepted());
    }

    @DisplayName("PATCH /api/comment/{comment_id} 요청을 보내면 comment_id를 가진 댓글의 내용이 수정된다.")
    @Test
    @WithCustomMockUser
    void 댓글_수정_API() throws Exception {
        // given
        String NEW_TEXT = TEXT + TEXT;
        CommentUpdateReqDto reqDto = CommentUpdateReqDto.builder()
                .postId(1L)
                .commentId(1L)
                .content(NEW_TEXT)
                .build();

        // when then
        mockMvc.perform(patch("/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isAccepted());
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

        Integer page = 0;
        Integer size = 10;
        CommentByPostReqDto reqDto = CommentByPostReqDto.builder()
                .postId(1L)
                .pageable(PageRequest.of(0, 10))
                .build();

        List<CommentResponseDto> response = List.of(
                CommentResponseDto.Of(comment),
                CommentResponseDto.Of(comment2)
        );
        when(commentService.getComments(any(CommentByPostReqDto.class))).thenReturn(response);

        // when
        MvcResult result = mockMvc.perform(get("/comment?postid=" + reqDto.getPostId() + "&page=" + page +"&size=" + size))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(TEXT))
                .andExpect(jsonPath("$[1].content").value(NEW_TEXT))
                .andReturn();
    }

}