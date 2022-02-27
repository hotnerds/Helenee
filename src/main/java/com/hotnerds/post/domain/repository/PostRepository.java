package com.hotnerds.post.domain.repository;

import com.hotnerds.post.domain.Post;
import org.apache.tomcat.jni.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByTitle(String title);

    @Query("select p from Post p where p.createdAt <= :after")
    List<Post> findAllPostsAfter(@Param("after") LocalDateTime after);
}