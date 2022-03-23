package com.hotnerds.post.domain.tag;

import com.hotnerds.post.domain.Post;
import com.hotnerds.tag.domain.Tag;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public PostTag(Long id, Post post, Tag tag) {
        this.id = id;
        this.post = post;
        this.tag = tag;
    }

    public PostTag(Post post, Tag tag) {
        this(null, post, tag);
    }

    public boolean isSameTag(Tag tag) {
        return Objects.equals(this.tag, tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostTag postTag = (PostTag) o;
        return Objects.equals(post, postTag.post) && Objects.equals(tag, postTag.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, tag);
    }
}
