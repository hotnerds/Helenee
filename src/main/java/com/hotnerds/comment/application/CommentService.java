package com.hotnerds.comment.application;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.comment.domain.Comment;
import com.hotnerds.post.domain.dto.CommentCreateReqDto;
import com.hotnerds.post.domain.dto.CommentDeleteReqDto;
import com.hotnerds.post.domain.dto.CommentResponseDto;
import com.hotnerds.post.domain.dto.CommentUpdateReqDto;
import com.hotnerds.comment.repository.CommentRepository;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.user.domain.User;
import com.hotnerds.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void addComment(CommentCreateReqDto reqDto) {
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

        commentRepository.save(comment);

        post.addComment(comment);
    }

    public void deleteComment(CommentDeleteReqDto reqDto, Long requesterId) {
        Post post = postRepository.findById(reqDto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        Comment comment = commentRepository.findById(reqDto.getCommentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        User user = userRepository.findById(comment.getWriter().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (!user.getId().equals(requesterId)) {
            throw new BusinessException(ErrorCode.INVALID_USER_EXCEPTION);
        }

        post.removeComment(reqDto.getCommentId());
        commentRepository.deleteById(reqDto.getCommentId());
    }

    public void updateComment(CommentUpdateReqDto reqDto, Long requesterId) {
        Post post = postRepository.findById(reqDto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        Comment comment = commentRepository.findById(reqDto.getCommentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        User user = userRepository.findById(comment.getWriter().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (!user.getId().equals(requesterId)) {
            throw new BusinessException(ErrorCode.INVALID_USER_EXCEPTION);
        }

        comment.updateContent(reqDto.getContent());
    }

    public CommentResponseDto getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION));
        return CommentResponseDto.builder()
                .commentList(post.getAllComments())
                .build();
    }
}
