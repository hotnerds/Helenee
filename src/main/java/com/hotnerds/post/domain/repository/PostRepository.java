package com.hotnerds.post.domain.repository;

import com.hotnerds.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
