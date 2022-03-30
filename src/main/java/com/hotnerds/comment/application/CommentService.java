package com.hotnerds.comment.application;

import com.hotnerds.comment.domain.Dto.*;
import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.repository.CommentRepository;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Comment addComment(CommentCreateReqDto reqDto) {
        if (!Comment.checkContentValid(reqDto.getContent())) {
            throw new BusinessException(ErrorCode.COMMENT_INVALID_EXCEPTION);
        }

        User user = userRepository.findById(reqDto.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        Post post = postRepository.findById(reqDto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        Comment comment = Comment.builder()
                .writer(user)
                .post(post)
                .content(reqDto.getContent())
                .build();

        Comment newComment = commentRepository.save(comment);

        post.addComment(newComment);

        return newComment;
    }

    public void deleteComment(CommentDeleteReqDto reqDto, Long requesterId) {
        Post post = postRepository.findById(reqDto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        Comment comment = commentRepository.findById(reqDto.getCommentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        User user = userRepository.findById(comment.getWriter().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (!user.getId().equals(requesterId)) {
            throw new BusinessException(ErrorCode.USER_INVALID_EXCEPTION);
        }

        post.removeComment(reqDto.getCommentId());
        commentRepository.deleteById(reqDto.getCommentId());
    }

    public Comment updateComment(CommentUpdateReqDto reqDto, Long requesterId) {
        Comment comment = commentRepository.findById(reqDto.getCommentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        User user = userRepository.findById(comment.getWriter().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (!user.getId().equals(requesterId)) {
            throw new BusinessException(ErrorCode.USER_INVALID_EXCEPTION);
        }

        comment.updateContent(reqDto.getContent());

        return comment;
    }

    public List<CommentResponseDto> getComments(CommentByPostReqDto reqDto) {
        Post post = postRepository.findById(reqDto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        return commentRepository.findAllByPost(post, reqDto.getPageable()).stream()
                .map(CommentResponseDto::Of)
                .collect(Collectors.toList());
    }
}
