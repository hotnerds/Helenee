package com.hotnerds.comment.repository;

import com.hotnerds.comment.domain.Comment;
import com.hotnerds.comment.domain.Dto.CommentResponseDto;
import com.hotnerds.post.domain.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.post = :post order by c.createdAt desc")
    List<Comment> findAllByPost(Post post, PageRequest pageable);

}
