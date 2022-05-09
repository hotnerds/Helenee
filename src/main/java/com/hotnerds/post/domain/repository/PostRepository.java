package com.hotnerds.post.domain.repository;

import com.hotnerds.post.domain.Post;
import com.hotnerds.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select distinct p from Post p " +
            "join fetch p.writer " +
            "order by p.createdAt desc")
    List<Post> findAllPosts(Pageable pageable);

    List<Post> findAllByTitle(String title, Pageable pageable);

    @Query("select p from Post p where p.writer = :writer order by p.createdAt desc")
    List<Post> findAllByWriter(@Param("writer") User writer, Pageable pageable);

    @Query("select distinct p from PostTag pt inner join pt.post p where pt.tag.name in :tagNames")
    List<Post> findAllByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);
}
