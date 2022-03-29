package com.hotnerds.comment.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotnerds.ControllerTest;
import com.hotnerds.comment.application.CommentService;
import com.hotnerds.comment.domain.Dto.CommentCreateReqDto;
import com.hotnerds.comment.domain.Dto.CommentDeleteReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest extends ControllerTest {

    @MockBean
    private CommentService commentService;

    final static String TEXT = "An apple a day keeps the doctor away";

    @DisplayName("POST /api/comment/ 요청을 보내면 DB에 새로운 댓글이 생성된다")
    @Test
    void 댓글_생성_API() throws Exception {
        // given
        CommentCreateReqDto reqDto = CommentCreateReqDto.builder()
                .userId(1L)
                .postId(1L)
                .content(TEXT)
                .build();

        // when then
        mockMvc.perform(post("/comment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(reqDto)))
                .andExpect(status().isOk());
    }

    @DisplayName("DELETE /api/comment/{comment_id} 요청을 보내면 Dto에 있는 정보에 해당하는 댓글이 삭제된다")
    @Test
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
                    .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(reqDto)))
                .andExpect(status().isAccepted());
    }

}