package com.hotnerds.post.domain.repository;

import com.hotnerds.post.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
