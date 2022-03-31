package com.hotnerds.comment.repository;

import com.hotnerds.comment.domain.Comment;
import com.hotnerds.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.post = :post order by c.createdAt asc")
    List<Comment> findAllByPost(@Param("post") Post post, Pageable pageable);

}
